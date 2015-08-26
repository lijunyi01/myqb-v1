package allcom.controller;

import allcom.entity.Account;
import allcom.service.*;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by ljy on 15/5/12.
 * class 上的@RequestMapping("/aaa")注释和方法上的@RequestMapping("/greeting")要叠加
 * http://localhost:8080/aaa/greeting?name=ljy  才能正常访问；若果class上没有@RequestMapping("/aaa")，则
 * 访问http://localhost:8080/greeting?name=ljy 即可
 */

@RestController
public class GenController {


    private static Logger log = LoggerFactory.getLogger(GenController.class);

    @Autowired
    private SessionService sessionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionCgService questionCgService;
    @Autowired
    private ClassTypeService classTypeService;
    @Autowired
    private NoteBookService noteBookService;

    // 通用接口,用于已经完成登录验证后的其它请求
    @RequestMapping(value = "/gi")
    public RetMessage generalInterface(
            @RequestParam(value="functionId",required = true) int functionId,
            @RequestParam(value = "umid",required = true)int umid,
            @RequestParam(value = "generalInput",required = true) String generalInput,
            @RequestParam(value = "sessionId",required = true)String sessionId,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request
    ) {
        log.info("In general interface,functionid is:"+functionId +"; general input params is:"+generalInput);
        RetMessage ret = null;
        String clientip = request.getRemoteAddr();


        if (sessionService.verifySessionId(umid, sessionId)) {
            Map<String, String> inputMap = GlobalTools.parseInput(generalInput);
            Account account = accountService.createAccountIfNotExist(umid);

            if (account != null) {
                if (functionId == 1) {
                    //登录验证，该步骤可以不用，非强制
                    ret = accountService.returnFail(area, "0");
                    log.info("login success! umid is:" + umid);
                }else if(functionId>1 && functionId<20){
                    ret = accountService.genCall(area,umid,sessionId,generalInput,functionId);
                    log.info("umid:" + umid + " functionId:"+functionId +" genCall result:" + ret.getErrorCode());
                }else if (functionId == 21) {
                    //设置本地存储的用户账户信息
                    int grade_i = -1000;
                    if(GlobalTools.isNumeric(inputMap.get("grade"))) {
                        grade_i = Integer.parseInt(inputMap.get("grade"));
                    }

                    if(grade_i <-100 || grade_i >1000){
                        ret = accountService.returnFail(area,"-14");
                    }else{
                        ret = accountService.setLocalInfo(area,umid,inputMap,grade_i);
                    }

                }else if (functionId == 22) {
                    //获取本地存储的用户账户信息
                    ret = accountService.getLocalInfo(area,umid);
                    log.info("umid:"+umid+" get localinfo result:" + ret.getErrorCode());
                }else if (functionId == 23) {
                    //获取科目大类信息
                    //http://localhost:8080/gi?functionId=23&umid=1&sessionId=111&generalInput=
                    ret = classTypeService.getClassTypeInfo(area);
                    log.info("umid:"+umid+" get classType result:" + ret.getErrorCode());
                }else if (functionId == 24) {
                    //获取科目子类信息
                    //http://localhost:8080/gi?functionId=24&umid=1&sessionId=111&generalInput=classType=1
                    int classType = GlobalTools.convertStringToInt(inputMap.get("classType"));
                    if(inputMap.size()!=1 || classType==-10000){
                        ret = classTypeService.returnFail(area, "-14");
                        log.info("general input param error:" + generalInput);
                    }else{
                        ret = classTypeService.getClassSubTypeInfo(area,classType);
                        log.info("umid:"+umid+" get classSubType result:" + ret.getErrorCode());
                    }

                }else if (functionId == 30) {
                    //存储题目信息(心得及正确／错误答案保存于数据库表myqb_answerandnote；创建题目时，子题的正确／错误答案及心得不一定有，可以后期添加)
                    //http://localhost:8080/gi?functionId=30&umid=1&sessionId=111&generalInput=grade=10<[CDATA]>questionType=101<[CDATA]>classType=1<[CDATA]>classSubType=1<[CDATA]>multiplexFlag=1<[CDATA]>subQuestionCount=2<[CDATA]>contentHeader=content-header-test<[CDATA]>subject=sub<[CDATA]>attachmentIds=<[CDATA]>subQuestions=seqId=1<[CDATA2]>qType=1<[CDATA2]>content=content1<[CDATA2]>attachedInfo=A:6<[CDATA3]>B:7<[CDATA3]>C:8<[CDATA3]>D:9<[CDATA2]>attachmentIds=<[CDATA2]>correctAnswer=C<[CDATA2]>wrongAnswer=A<[CDATA2]>note=note1<[CDATA1]>seqId=2<[CDATA2]>qType=1<[CDATA2]>content=content2<[CDATA2]>attachedInfo=A:1<[CDATA3]>B:2<[CDATA3]>C:3<[CDATA3]>D:4<[CDATA2]>attachmentIds=<[CDATA2]>correctAnswer=A<[CDATA2]>wrongAnswer=D<[CDATA2]>note=note2
                    if(inputMap.size()!=10){
                        ret = questionService.returnFail(area, "-14");
                        log.info("general input param error:" + generalInput);
                    }else{
                        long questionid = questionService.createQuestion(umid, inputMap);
                        if(questionid != -1){
                            ret = questionService.returnFail(area, "0");
                            ret.setRetContent("questionId="+questionid);
                        }else{
                            ret = questionService.returnFail(area, "-18");
                        }
                    }
                }else if (functionId == 31) {
                    //修改题目信息(包含心得及正确／错误答案)
                    //http://localhost:8080/gi?functionId=31&umid=1&sessionId=111&generalInput=questionId=18<[CDATA]>grade=10<[CDATA]>questionType=101<[CDATA]>classType=1<[CDATA]>classSubType=1<[CDATA]>multiplexFlag=1<[CDATA]>subQuestionCount=2<[CDATA]>contentHeader=content-header-test<[CDATA]>subject=sub<[CDATA]>attachmentIds=<[CDATA]>subQuestions=seqId=1<[CDATA2]>qType=1<[CDATA2]>content=content1<[CDATA2]>attachedInfo=A:6<[CDATA3]>B:7<[CDATA3]>C:8<[CDATA3]>D:9<[CDATA2]>attachmentIds=<[CDATA2]>correctAnswer=C<[CDATA2]>wrongAnswer=A<[CDATA2]>note=note1<[CDATA1]>seqId=2<[CDATA2]>qType=1<[CDATA2]>content=content2<[CDATA2]>attachedInfo=A:1<[CDATA3]>B:2<[CDATA3]>C:3<[CDATA3]>D:4<[CDATA2]>attachmentIds=<[CDATA2]>correctAnswer=A<[CDATA2]>wrongAnswer=D<[CDATA2]>note=note2
                    if(inputMap.size()!=11){
                        ret = questionService.returnFail(area, "-14");
                        log.info("general input param error:" + generalInput);
                    }else{
                        if(questionService.modifyQuestion(umid, inputMap)){
                            ret = questionService.returnFail(area, "0");
                        }else{
                            ret = questionService.returnFail(area, "-18");
                        }
                    }
                }else if (functionId == 32) {
                    //修改含心得及正确／错误答案(不包含题目本身);必须包含所有子题的答案和心得
                    //http://localhost:8080/gi?functionId=32&umid=1&sessionId=111&generalInput=questionId=21<[CDATA]>subQuestions=seqId=1<[CDATA2]>correctAnswer=C<[CDATA2]>wrongAnswer=A<[CDATA2]>note=note1<[CDATA1]>seqId=2<[CDATA2]>correctAnswer=A<[CDATA2]>wrongAnswer=D<[CDATA2]>note=note21
                    if(inputMap.size()!=2){
                        ret = questionService.returnFail(area, "-14");
                        log.info("general input param error:" + generalInput);
                    }else{
                        if(questionService.modifyAnswerAndNote(umid,inputMap)){
                            ret = questionService.returnFail(area, "0");
                        }else{
                            ret = questionService.returnFail(area, "-18");
                        }
                    }
                }else if (functionId == 33) {
                    //存储及修改题目草稿信息(心得及正确／错误答案保存于数据库表myqb_answerandnote；创建题目时，子题的正确／错误答案及心得不一定有，可以后期添加)
                    //http://localhost:8080/gi?functionId=33&umid=1&sessionId=111&generalInput=questionId=<[CDATA]>grade=10<[CDATA]>questionType=101<[CDATA]>classType=1<[CDATA]>classSubType=1<[CDATA]>multiplexFlag=1<[CDATA]>subQuestionCount=2<[CDATA]>contentHeader=content-header-test<[CDATA]>subject=sub<[CDATA]>attachmentIds=<[CDATA]>subQuestions=seqId=1<[CDATA2]>qType=1<[CDATA2]>content=content1<[CDATA2]>attachedInfo=A:6<[CDATA3]>B:7<[CDATA3]>C:8<[CDATA3]>D:9<[CDATA2]>attachmentIds=<[CDATA2]>correctAnswer=C<[CDATA2]>wrongAnswer=A<[CDATA2]>note=note1<[CDATA1]>seqId=2<[CDATA2]>qType=1<[CDATA2]>content=content2<[CDATA2]>attachedInfo=A:1<[CDATA3]>B:2<[CDATA3]>C:3<[CDATA3]>D:4<[CDATA2]>attachmentIds=<[CDATA2]>correctAnswer=A<[CDATA2]>wrongAnswer=D<[CDATA2]>note=note2
                    if(inputMap.size()!=11){
                        ret = questionCgService.returnFail(area, "-14");
                        log.info("general input param error:" + generalInput);
                    }else{
                        long questionid = questionCgService.createQuestionCg(umid, inputMap);
                        if(questionid != -1){
                            ret = questionCgService.returnFail(area, "0");
                            ret.setRetContent("questionId="+questionid);
                        }else{
                            ret = questionCgService.returnFail(area, "-18");
                        }
                    }
                }else if (functionId == 34) {
                    //删除某条草稿
                    //http://localhost:8080/gi?functionId=34&umid=1&sessionId=111&generalInput=questionId=1
                    if(inputMap.size()!=1){
                        ret = questionCgService.returnFail(area, "-14");
                        log.info("general input param error:" + generalInput);
                    }else{
                        if(questionCgService.deleteQuestionCg(umid, inputMap)){
                            ret = questionCgService.returnFail(area, "0");
                        }else{
                            ret = questionCgService.returnFail(area, "-18");
                        }
                    }
                }else if (functionId == 35) {
                    //删除某条题目
                    //http://localhost:8080/gi?functionId=35&umid=1&sessionId=111&generalInput=questionId=1
                    if(inputMap.size()!=1){
                        ret = questionService.returnFail(area, "-14");
                        log.info("general input param error:" + generalInput);
                    }else{
                        if(questionService.deleteQuestion(umid, inputMap)){
                            ret = questionService.returnFail(area, "0");
                        }else{
                            ret = questionService.returnFail(area, "-18");
                        }
                    }
                }else if(functionId == 40) {
                    //http://localhost:8080/gi?functionId=40&umid=1&generalInput=grade=10<[CDATA]>classType=1&sessionId=111
                    //获取符合条件的题目id（按question的各种类型核对）
                    ret = questionService.getIdsByType(umid, inputMap, area);
                }else if(functionId == 41) {
                    //http://localhost:8080/gi?functionId=41&umid=1&generalInput=content=a&sessionId=111
                    //获取符合条件的题目id（按question的内容查找）
                    ret = questionService.getIdsByContent(umid, inputMap.get("content"), area);
                }else if(functionId == 42) {
                    //http://localhost:8080/gi?functionId=42&umid=1&generalInput=&sessionId=111
                    //获取符合条件的题目草稿id（按umid查找）
                    ret = questionCgService.getCgIds(umid,area);
                }else if(functionId == 50){
                    //新增订正本组
                    //http://localhost:8080/gi?functionId=50&umid=1&generalInput=groupName=james的订正本组&sessionId=111
                    ret = noteBookService.createNoteBookGroup(umid,inputMap.get("groupName"),area);
                }else if(functionId == 51){
                    //修改订正本组组名
                    //http://localhost:8080/gi?functionId=51&umid=1&generalInput=groupId=1<[CDATA]>groupName=james的订正本组&sessionId=111
                    ret = noteBookService.modifyNoteBookGroup(umid,inputMap.get("groupId"), inputMap.get("groupName"),area);
                }else if(functionId == 52){
                    //删除订正本组
                    //http://localhost:8080/gi?functionId=52&umid=1&generalInput=groupId=1&sessionId=111
                    ret = noteBookService.deleteNoteBookGroup(umid,inputMap.get("groupId"),area);
                }

            }else{
                //获取account信息失败
                ret = accountService.returnFail(area, "-15");
                log.info("umid:" + umid + " failed to get account");
            }

        }else {
            ret = sessionService.returnFail(area, "-4");
            log.info("umid:" + umid + " failed to check sessionid:" + sessionId);
        }


        return ret;
    }

}
