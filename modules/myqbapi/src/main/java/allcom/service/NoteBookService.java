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

    //group增
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
            //名称冲突
            ret.setErrorCode("-27");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-27"));
        }
        return ret;
    }

    //group改
    public RetMessage modifyNoteBookGroup(int umid,String groupId,String groupName,String area){
        RetMessage ret = new RetMessage();
        //String retContent="";
        long gId = GlobalTools.convertStringToLong(groupId);
        if(GlobalTools.stringParamHasNullOrEmpty(groupName) || gId == -10000){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            NoteBookGroup noteBookGroup = noteBookGroupRepository.findOne(gId);
            if(noteBookGroup == null){
                //未找到符合条件的数据
                ret.setErrorCode("-24");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-24"));
            }else if(noteBookGroupRepository.findByUmidAndName(umid,groupName)!=null){
                //名称冲突
                ret.setErrorCode("-27");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-27"));
            }else if(noteBookGroup.getUmid()==umid){
                //umid校验失败
                ret.setErrorCode("-28");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-28"));
            }else{
                noteBookGroup.setName(groupName);
                if (noteBookGroupRepository.save(noteBookGroup) != null) {
                    ret.setErrorCode("0");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                }
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

    public RetMessage showNoteBookGroup(int umid,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        List<NoteBookGroup> noteBookGroupList = noteBookGroupRepository.findByUmid(umid);
        if(noteBookGroupList !=null){
            for(NoteBookGroup noteBookGroup:noteBookGroupList){
                if(retContent.equals("")){
                    retContent += noteBookGroup.getId()+":"+noteBookGroup.getName();
                }else{
                    retContent += "<[CDATA]>"+noteBookGroup.getId()+":"+noteBookGroup.getName();
                }
            }
            ret.setErrorCode("0");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
            ret.setRetContent(retContent);
        }
        return ret;
    }

    public RetMessage createNoteBook(int umid,String bookName,String groupId,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        NoteBook noteBook = noteBookRepository.findByUmidAndName(umid,bookName);
        if(noteBook ==null){
            noteBook = new NoteBook(umid,bookName);
            long gId = GlobalTools.convertStringToLong(groupId);
            if(gId != -10000){
                NoteBookGroup noteBookGroup = noteBookGroupRepository.findOne(gId);
                if(noteBookGroup!=null && noteBookGroup.getUmid() == umid) {
                    noteBook.setGroupId(gId);
                }else{
                    ret.setErrorCode("-24");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-24"));
                }
            }
            //groupId对得上才创建，否则返回－24
            if(!ret.getErrorCode().equals("-24")) {
                NoteBook noteBook1 = noteBookRepository.save(noteBook);
                if (noteBook1 != null) {
                    ret.setErrorCode("0");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                    retContent += "id:" + noteBook1.getId();
                    ret.setRetContent(retContent);
                }
            }
        }else{
            //同名订正本已存在
            ret.setErrorCode("-25");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-25"));
        }
        return ret;
    }

    public RetMessage modifyNoteBook(int umid,String id,String bookName,String groupId,String area) {
        RetMessage ret = new RetMessage();
        long bookId = GlobalTools.convertStringToLong(id);
        long gId = GlobalTools.convertStringToLong(groupId);
        if(bookId == -10000){
            ret.setErrorCode("-14");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-14"));
        }else {
            NoteBook noteBook = noteBookRepository.findOne(bookId);
            if(noteBook!=null && noteBook.getUmid()==umid){
                if(!bookName.equals("")){
                    noteBook.setName(bookName);
                }
                if(gId == -10000){
                    //设置订正本不属于任何组
                    noteBook.setGroupId(-1);
                }else{
                    NoteBookGroup noteBookGroup = noteBookGroupRepository.findOne(gId);
                    if(noteBookGroup!=null && noteBookGroup.getUmid() == umid) {
                        noteBook.setGroupId(gId);
                    }else{
                        ret.setErrorCode("-24");
                        ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-24"));
                    }
                }
                if(!ret.getErrorCode().equals("-24")&& noteBookRepository.save(noteBook)!=null){
                    ret.setErrorCode("0");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                }
            }else {
                //未找到符合条件的数据
                ret.setErrorCode("-24");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-24"));
            }
        }
        return ret;
    }
}