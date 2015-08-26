package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.NoteBookGroupRepository;
import allcom.dao.NoteBookRepository;
import allcom.entity.NoteBook;
import allcom.entity.NoteBookGroup;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ljy on 15/6/10.
 * ok
 */
@Service
public class NoteBookService {
    private static Logger log = LoggerFactory.getLogger(NoteBookService.class);

    @Autowired
    private NoteBookRepository noteBookRepository;
    @Autowired
    private NoteBookGroupRepository noteBookGroupRepository;

    public RetMessage returnFail(String area,String errorCode){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorCode(errorCode);
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,errorCode));
        return retMessage;
    }


//    public RetMessage getClassSubTypeInfo(String area,int classType){
//        RetMessage retMessage = new RetMessage();
//        List<ClassSubType> classSubTypeList = classSubTypeRepository.findByClassType(classType);
//        String retContent="";
//        for(ClassSubType classSubType:classSubTypeList){
//            if(retContent.equals("")){
//                if(area.equals("en")){
//                    retContent = classSubType.getClassSubType() + ":" + classSubType.getSubTypeDescEn();
//                }else {
//                    retContent = classSubType.getClassSubType() + ":" + classSubType.getSubTypeDesc();
//                }
//            }else{
//                if(area.equals("en")){
//                    retContent = retContent + "<[CDATA]>" + classSubType.getClassSubType() + ":" + classSubType.getSubTypeDescEn();
//                }else {
//                    retContent = retContent + "<[CDATA]>" + classSubType.getClassSubType() + ":" + classSubType.getSubTypeDesc();
//                }
//            }
//        }
//        retMessage.setErrorCode("0");
//        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,"0"));
//        retMessage.setRetContent(retContent);
//        return retMessage;
//    }

    public RetMessage createNoteBookGroup(int umid,String groupName,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        NoteBookGroup noteBookGroup = noteBookGroupRepository.findByUmidAndName(umid,groupName);
        if(noteBookGroup ==null){
            noteBookGroup = new NoteBookGroup(umid,groupName);
            NoteBookGroup noteBookGroup1 = noteBookGroupRepository.save(noteBookGroup);
            if(noteBookGroup1 != null){
                ret.setErrorCode("0");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"0"));
                retContent += "groupId:"+noteBookGroup1.getId();
                ret.setRetContent(retContent);
            }
        }else{
            //同名组已存在
            ret.setErrorCode("-25");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-25"));
        }
        return ret;
    }

    public RetMessage modifyNoteBookGroup(int umid,String groupId,String groupName,String area){
        RetMessage ret = new RetMessage();
        //String retContent="";
        long gId = GlobalTools.convertStringToLong(groupId);
        if(gId == -10000){
            ret.setErrorCode("-14");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-14"));
        }else {
            NoteBookGroup noteBookGroup = noteBookGroupRepository.findOne(gId);
            if (noteBookGroup != null && noteBookGroup.getUmid()==umid) {
                noteBookGroup.setName(groupName);
                if (noteBookGroupRepository.save(noteBookGroup) != null) {
                    ret.setErrorCode("0");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                }
            } else {
                //未找到符合条件的数据
                ret.setErrorCode("-24");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-24"));
            }
        }
        return ret;
    }

    public RetMessage deleteNoteBookGroup(int umid,String groupId,String area){
        RetMessage ret = new RetMessage();
        //String retContent="";
        long gId = GlobalTools.convertStringToLong(groupId);
        if(gId == -10000){
            ret.setErrorCode("-14");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-14"));
        }else {
            NoteBookGroup noteBookGroup = noteBookGroupRepository.findOne(gId);
            if (noteBookGroup != null && noteBookGroup.getUmid()==umid) {
                //还有订正本属于该组，不能删
                List<NoteBook> noteBookList = noteBookRepository.findByUmidAndGroupId(umid,gId);
                if(noteBookList!=null && !noteBookList.isEmpty()){
                    ret.setErrorCode("-26");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-26"));
                }else{
                    noteBookGroupRepository.delete(gId);
                    ret.setErrorCode("0");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                }
            } else {
                //未找到符合条件的数据
                ret.setErrorCode("-24");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-24"));
            }
        }
        return ret;
    }

}