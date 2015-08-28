package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.NoteBookGroupRepository;
import allcom.dao.NoteBookRepository;
import allcom.dao.QuestionRepository;
import allcom.entity.NoteBook;
import allcom.entity.NoteBookGroup;
import allcom.entity.Question;
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
    @Autowired
    private QuestionRepository questionRepository;

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
        if(GlobalTools.stringParamHasNullOrEmpty(groupName)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            NoteBookGroup noteBookGroup = noteBookGroupRepository.findByUmidAndName(umid, groupName);
            if (noteBookGroup == null) {
                noteBookGroup = new NoteBookGroup(umid, groupName);
                NoteBookGroup noteBookGroup1 = noteBookGroupRepository.save(noteBookGroup);
                if (noteBookGroup1 != null) {
                    ret.setErrorCode("0");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                    retContent += "groupId:" + noteBookGroup1.getId();
                    ret.setRetContent(retContent);
                }
            }else {
                //名称冲突
                ret.setErrorCode("-27");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-27"));
            }
        }
        return ret;
    }

    //group改(只能改名称)
    public RetMessage modifyNoteBookGroup(int umid,String groupId,String groupName,String area){
        RetMessage ret = new RetMessage();
        long gId = GlobalTools.convertStringToLong(groupId);
        if(GlobalTools.stringParamHasNullOrEmpty(groupName) || !isGroupIdValid(umid,gId,false)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            NoteBookGroup noteBookGroup = noteBookGroupRepository.findOne(gId);
            if(noteBookGroup == null){
                //因为之前已经校验过gId，如果还进入该流程则说明是数据库查询出错的情况
                ret.setErrorCode("-30");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
                log.info("noteBookGroupRepository.findOne(gId):failed!!!");
            }else if(noteBookGroupRepository.findByUmidAndName(umid,groupName)!=null){
                //名称冲突
                ret.setErrorCode("-27");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-27"));
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

    //group删
    public RetMessage deleteNoteBookGroup(int umid,String groupId,String area){
        RetMessage ret = new RetMessage();
        //String retContent="";
        long gId = GlobalTools.convertStringToLong(groupId);
        if(!isGroupIdValid(umid,gId,false)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            NoteBookGroup noteBookGroup = noteBookGroupRepository.findOne(gId);
            if (noteBookGroup != null) {
                //还有订正本属于该组，不能删
                List<NoteBook> noteBookList = noteBookRepository.findByUmidAndGroupId(umid,gId);
                if(noteBookList != null) {
                    if (!noteBookList.isEmpty()) {
                        ret.setErrorCode("-26");
                        ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-26"));
                    } else {
                        noteBookGroupRepository.delete(gId);
                        ret.setErrorCode("0");
                        ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                    }
                }else{
                    //数据库查询一定出异常了
                    ret.setErrorCode("-30");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
                    log.info("noteBookRepository.findByUmidAndGroupId(umid,gId):failed!!!");
                }
            } else {
                //因为之前已经校验过gId，如果还进入该流程则说明是数据库查询出错的情况
                ret.setErrorCode("-30");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
                log.info("noteBookGroupRepository.findOne(gId):failed!!!");
            }
        }
        return ret;
    }

    //group查
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
        }else{
            //数据库查询一定出异常了
            ret.setErrorCode("-30");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
            log.info("noteBookGroupRepository.findByUmid(umid):failed!!!");
        }
        return ret;
    }

    //订正本增
    public RetMessage createNoteBook(int umid,String bookName,String groupId,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        long gId = GlobalTools.convertStringToLong(groupId);
        //此处参数校验允许groupId为空或不可转为数字，即gId=-10000的情况，此情况被认为不设置组信息；通过校验的groupId要么＝－10000，要么就是在组里实际存在的
        if(GlobalTools.stringParamHasNullOrEmpty(bookName) || !isGroupIdValid(umid,gId,true)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            NoteBook noteBook = noteBookRepository.findByUmidAndName(umid, bookName);
            if (noteBook == null) {
                noteBook = new NoteBook(umid, bookName);
                if (gId != -10000) {
                    noteBook.setGroupId(gId);
                }
                NoteBook noteBook1 = noteBookRepository.save(noteBook);
                if (noteBook1 != null) {
                    ret.setErrorCode("0");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                    retContent += "id:" + noteBook1.getId();
                    ret.setRetContent(retContent);
                }

            }else {
                //名称冲突
                ret.setErrorCode("-27");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-27"));
            }
        }
        return ret;
    }

    //订正本改名称
    public RetMessage modifyNoteBookName(int umid,String id,String bookName,String area) {
        RetMessage ret = new RetMessage();
        long bookId = GlobalTools.convertStringToLong(id);
        if(bookId == -10000 || GlobalTools.stringParamHasNullOrEmpty(bookName)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            NoteBook noteBook = noteBookRepository.findOne(bookId);
            if(noteBook!=null && noteBook.getUmid()==umid){
                NoteBook noteBook1 = noteBookRepository.findByUmidAndName(umid, bookName);
                if(noteBook1 == null){
                    noteBook.setName(bookName);
                    if(noteBookRepository.save(noteBook)!=null){
                        ret.setErrorCode("0");
                        ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                    }
                }else{
                    //名称冲突
                    ret.setErrorCode("-27");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-27"));
                }
            }else {
                //未找到符合条件的数据
                ret.setErrorCode("-24");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-24"));
            }
        }
        return ret;
    }

    //订正本改对应的组
    public RetMessage modifyNoteBookGroupId(int umid,String id,String groupId,String area) {
        RetMessage ret = new RetMessage();
        long bookId = GlobalTools.convertStringToLong(id);
        long gId = GlobalTools.convertStringToLong(groupId);
        if(bookId == -10000 || !isGroupIdValid(umid,gId,true)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            NoteBook noteBook = noteBookRepository.findOne(bookId);
            if(noteBook!=null && noteBook.getUmid()==umid){
                if(gId != -10000){
                    noteBook.setGroupId(gId);
                }else{
                    noteBook.setGroupId(-1);
                }
                if(noteBookRepository.save(noteBook)!=null){
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

    //订正本删
    public RetMessage deleteNoteBook(int umid,String bookId,String area){
        RetMessage ret = new RetMessage();
        long bId = GlobalTools.convertStringToLong(bookId);
        if(!isBookIdValid(umid, bId, false)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            NoteBook noteBook = noteBookRepository.findOne(bId);
            if (noteBook != null) {
                //还有题目或废件属于该订正本，不能删
                List<Question> questionList = questionRepository.findByUmidAndNotebookId(umid,bId);
                if(questionList != null) {
                    if (!questionList.isEmpty()) {
                        ret.setErrorCode("-26");
                        ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-26"));
                    } else {
                        noteBookRepository.delete(bId);
                        ret.setErrorCode("0");
                        ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                    }
                }else{
                    //数据库查询一定出异常了
                    ret.setErrorCode("-30");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
                    log.info("questionRepository.findByUmidAndNotebookId(umid,bId):failed!!!");
                }
            } else {
                //因为之前已经校验过bId，如果还进入该流程则说明是数据库查询出错的情况
                ret.setErrorCode("-30");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
                log.info("noteBookRepository.findOne(bId):failed!!!");
            }
        }
        return ret;
    }

    //订正本查
    public RetMessage showNoteBook(int umid,String groupId,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        long gId = GlobalTools.convertStringToLong(groupId);
        if(!isGroupIdValid(umid,gId,true)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            List<NoteBook> noteBookList = null;
            if(gId == -10000){
                noteBookList = noteBookRepository.findByUmid(umid);
            }else{
                noteBookList = noteBookRepository.findByUmidAndGroupId(umid,gId);
            }
            if (noteBookList != null) {
                for (NoteBook noteBook : noteBookList) {
                    if (retContent.equals("")) {
                        retContent += noteBook.getId() + ":" + noteBook.getName() + ":" + noteBook.getGroupId();
                    } else {
                        retContent += "<[CDATA]>"  + noteBook.getId() + ":" + noteBook.getName() + ":" + noteBook.getGroupId();
                    }
                }
                ret.setErrorCode("0");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                ret.setRetContent(retContent);
            } else {
                //数据库查询一定出异常了
                ret.setErrorCode("-30");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
                log.info("noteBookRepository.findByUmid(umid):failed!!!");
            }
        }
        return ret;
    }

    //判断groupId是否合法（主要判断客户端提供的groupId是否是在订正本组表里存在的，且与该用户相关）
    private boolean isGroupIdValid(int umid,long groupId,boolean isNullValid){
        boolean ret = false;
        if(groupId == -10000){
            if(isNullValid) {
                ret = true;
            }
        }else{
            NoteBookGroup noteBookGroup = noteBookGroupRepository.findOne(groupId);
            if(noteBookGroup !=null && noteBookGroup.getUmid() == umid){
                ret = true;
            }
        }
        return ret;
    }

    //判断订正本Id是否合法（主要判断客户端提供的bookId是否是在订正本表里存在的，且与该用户相关）
    private boolean isBookIdValid(int umid,long bookId,boolean isNullValid){
        boolean ret = false;
        if(bookId == -10000){
            if(isNullValid) {
                ret = true;
            }
        }else{
            NoteBook noteBook = noteBookRepository.findOne(bookId);
            if(noteBook !=null && noteBook.getUmid() == umid){
                ret = true;
            }
        }
        return ret;
    }
}