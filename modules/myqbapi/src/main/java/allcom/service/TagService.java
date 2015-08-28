package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.QuestionRepository;
import allcom.dao.TagQuestionRelationRepository;
import allcom.dao.TagRepository;
import allcom.entity.NoteBookGroup;
import allcom.entity.Question;
import allcom.entity.Tag;
import allcom.entity.TagQuestionRelation;
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
public class TagService {
    private static Logger log = LoggerFactory.getLogger(TagService.class);

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagQuestionRelationRepository tagQuestionRelationRepository;
    @Autowired
    private QuestionRepository questionRepository;

    public RetMessage returnFail(String area,String errorCode){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorCode(errorCode);
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,errorCode));
        return retMessage;
    }

    //tag增
    public RetMessage createTag(int umid,String tagName,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        if(GlobalTools.stringParamHasNullOrEmpty(tagName)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            Tag tag = tagRepository.findByUmidAndName(umid,tagName);
            if (tag == null) {
                tag = new Tag(umid, tagName);
                Tag tag1 = tagRepository.save(tag);
                if (tag1 != null) {
                    ret.setErrorCode("0");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                    retContent += "tagId:" + tag1.getId();
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

    //tag改(只能改名称)
    public RetMessage modifyTag(int umid,String tagId,String tagName,String area){
        RetMessage ret = new RetMessage();
        long tId = GlobalTools.convertStringToLong(tagId);
        if(GlobalTools.stringParamHasNullOrEmpty(tagName) || !isTagIdValid(umid,tId,false)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            Tag tag = tagRepository.findOne(tId);
            if(tag == null){
                //因为之前已经校验过tId，如果还进入该流程则说明是数据库查询出错的情况
                ret.setErrorCode("-30");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
                log.info("tagRepository.findOne(tId):failed!!!");
            }else if(tagRepository.findByUmidAndName(umid,tagName)!=null){
                //名称冲突
                ret.setErrorCode("-27");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-27"));
            }else{
                tag.setName(tagName);
                if (tagRepository.save(tag) != null) {
                    ret.setErrorCode("0");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                }
            }
        }
        return ret;
    }

    //tag删
    public RetMessage deleteTag(int umid,String tagId,String area){
        RetMessage ret = new RetMessage();
        //String retContent="";
        long tId = GlobalTools.convertStringToLong(tagId);
        if(!isTagIdValid(umid, tId, false)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            Tag tag = tagRepository.findOne(tId);
            if(tag != null) {
                //还有题目贴由该标签，不能删（包括废件箱的题目）
                List<TagQuestionRelation> tagQuestionRelationList = tagQuestionRelationRepository.findByUmidAndTagId(umid,tId);
                if(tagQuestionRelationList != null) {
                    if (!tagQuestionRelationList.isEmpty()) {
                        ret.setErrorCode("-26");
                        ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-26"));
                    }else {
                        tagRepository.delete(tId);
                        ret.setErrorCode("0");
                        ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                    }
                }else{
                    //数据库查询一定出异常了
                    ret.setErrorCode("-30");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
                    log.info("tagQuestionRelationRepository.findByUmidAndTagId(umid,tId):failed!!!");
                }
            }else {
                //因为之前已经校验过tId，如果还进入该流程则说明是数据库查询出错的情况
                ret.setErrorCode("-30");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
                log.info("tagRepository.findOne(tId):failed!!!");
            }
        }
        return ret;
    }

    //tag查(含标签对应的题目数量)
    public RetMessage showTag(int umid,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        List<Tag> tagList = tagRepository.findByUmid(umid);
        if(tagList !=null){
            for(Tag tag:tagList){
                if(retContent.equals("")){
                    retContent += tag.getId()+":"+tag.getName() +":"+getQuestionNumberByTag(umid,tag.getId());
                }else{
                    retContent += "<[CDATA]>"+tag.getId()+":"+tag.getName() +":"+getQuestionNumberByTag(umid,tag.getId());
                }
            }
            ret.setErrorCode("0");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
            ret.setRetContent(retContent);
        }else{
            //数据库查询一定出异常了
            ret.setErrorCode("-30");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
            log.info("tagRepository.findByUmid(umid):failed!!!");
        }
        return ret;
    }

    //给题目打标签
    public RetMessage addTagToQuestion(int umid,String tagId,String questionId, String area){
        RetMessage ret = new RetMessage();
        long tId = GlobalTools.convertStringToLong(tagId);
        long qId = GlobalTools.convertStringToLong(questionId);
        if(!isTagIdValid(umid,tId,false) || !isQuestionIdValid(umid,qId,false)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else{
            TagQuestionRelation tagQuestionRelation = tagQuestionRelationRepository.findByUmidAndTagIdAndQuestionId(umid,tId,qId);
            if(tagQuestionRelation == null){
                tagQuestionRelation = new TagQuestionRelation(umid,tId,qId);
                if(tagQuestionRelationRepository.save(tagQuestionRelation)!=null){
                    ret.setErrorCode("0");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                }
            }else{
                //已经打过该标签了
                ret.setErrorCode("-27");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-27"));
            }
        }
        return ret;
    }

    //给题目去除标签
    public RetMessage delTagFromQuestion(int umid,String tagId,String questionId, String area){
        RetMessage ret = new RetMessage();
        long tId = GlobalTools.convertStringToLong(tagId);
        long qId = GlobalTools.convertStringToLong(questionId);
        if(!isTagIdValid(umid,tId,false) || !isQuestionIdValid(umid,qId,false)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else{
            TagQuestionRelation tagQuestionRelation = tagQuestionRelationRepository.findByUmidAndTagIdAndQuestionId(umid,tId,qId);
            if(tagQuestionRelation != null){
                long id = tagQuestionRelation.getId();
                tagQuestionRelationRepository.delete(id);
                if(!tagQuestionRelationRepository.exists(id)) {
                    ret.setErrorCode("0");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                }
            }else{
                //未找到符合条件的数据
                ret.setErrorCode("-24");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-24"));
            }
        }
        return ret;
    }

    public RetMessage getNumberByTag(int umid,String tagId,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        long tId = GlobalTools.convertStringToLong(tagId);
        if(!isTagIdValid(umid, tId, false)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            List<TagQuestionRelation> tagQuestionRelationList = tagQuestionRelationRepository.findByUmidAndTagId(umid,tId);
            if (tagQuestionRelationList != null) {
                retContent += "summary:" + tagQuestionRelationList.size();
                ret.setErrorCode("0");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                ret.setRetContent(retContent);
            } else {
                //数据库查询异常了
                ret.setErrorCode("-30");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
                log.info("tagQuestionRelationRepository.findByUmidAndTagId(umid,tId):failed!!!");
            }
        }
        return ret;
    }

    private int getQuestionNumberByTag(int umid,long tagId){
        int ret = 0;
        List<TagQuestionRelation> tagQuestionRelationList = tagQuestionRelationRepository.findByUmidAndTagId(umid,tagId);
        if(tagQuestionRelationList!=null){
            ret = tagQuestionRelationList.size();
        }else{
            log.info("tagQuestionRelationRepository.findByUmidAndTagId(umid,tagId):failed!!!");
        }
        return ret;
    }

    public RetMessage modifyTagFromQuestion(int umid,String questionId,String delTagId,String addTagId,String addTagName,String area){
        RetMessage ret = new RetMessage();
        long qId = GlobalTools.convertStringToLong(questionId);
        long delTId = GlobalTools.convertStringToLong(delTagId);
        long addTId = GlobalTools.convertStringToLong(addTagId);
        if(!isQuestionIdValid(umid,qId,false) || !isTagIdValid(umid,delTId,false) || delTId == addTId) {
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-29"));
        }else if(!isTagIdValid(umid,addTId,false) && GlobalTools.stringParamHasNullOrEmpty(addTagName)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-29"));
        }else{
            if(!isTagIdValid(umid,addTId,false)){
                //标签尚不存在的的情况
                //添加标签本身
                Tag tag = tagRepository.findByUmidAndName(umid,addTagName);
                if (tag == null) {
                    tag = new Tag(umid, addTagName);
                    Tag tag1 = tagRepository.save(tag);
                    if (tag1 != null) {
                        addTId = tag1.getId();
                    }else{
                        log.info("add tag failed!!!");
                    }
                }else {
                    //名为addTagName的标签已存在了
                    addTId = tag.getId();
                    log.info("tag already exists:" + tag.getName());
                }
            }

            //再次校验addIid
            if(isTagIdValid(umid,addTId,false)) {
                //为题目增加一个标签
                TagQuestionRelation tagQuestionRelation1 = tagQuestionRelationRepository.findByUmidAndTagIdAndQuestionId(umid, addTId, qId);
                if (tagQuestionRelation1 == null) {
                    tagQuestionRelation1 = new TagQuestionRelation(umid, addTId, qId);
                    tagQuestionRelationRepository.save(tagQuestionRelation1);
                } else {
                    log.info("tag already exists!");
                }
                //从题目上去除原标签
                TagQuestionRelation tagQuestionRelation = tagQuestionRelationRepository.findByUmidAndTagIdAndQuestionId(umid, delTId, qId);
                if (tagQuestionRelation != null) {
                    tagQuestionRelationRepository.delete(tagQuestionRelation);
                }
                ret.setErrorCode("0");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
            }else{
                ret.setErrorCode("-31");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-31"));
            }
        }
        return ret;
    }

    //判断标签Id是否合法（主要判断客户端提供的tagId是否是在标签表里存在的，且与该用户相关）
    private boolean isTagIdValid(int umid,long tagId,boolean isNullValid){
        boolean ret = false;
        if(tagId == -10000){
            if(isNullValid) {
                ret = true;
            }
        }else{
            Tag tag = tagRepository.findOne(tagId);
            if(tag !=null && tag.getUmid() == umid){
                ret = true;
            }
        }
        return ret;
    }

    //判断questionId是否有效
    private boolean isQuestionIdValid(int umid,long questionId,boolean isNullValid){
        boolean ret = false;
        if(questionId == -10000){
            if(isNullValid) {
                ret = true;
            }
        }else{
            Question question = questionRepository.findOne(questionId);
            if(question !=null && question.getUmid() == umid){
                ret = true;
            }
        }
        return ret;
    }
}