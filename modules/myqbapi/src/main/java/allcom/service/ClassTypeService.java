package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.ClassSubTypeRepository;
import allcom.dao.ClassTypeRepository;
import allcom.entity.ClassSubType;
import allcom.entity.ClassType;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ljy on 15/6/10.
 * ok
 */
@Service
public class ClassTypeService {
    private static Logger log = LoggerFactory.getLogger(ClassTypeService.class);

    @Autowired
    private ClassTypeRepository classTypeRepository;
    @Autowired
    private ClassSubTypeRepository classSubTypeRepository;

    public RetMessage returnFail(String area,String errorCode){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorCode(errorCode);
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,errorCode));
        return retMessage;
    }

    public RetMessage getClassTypeInfo(String area){
        RetMessage retMessage = new RetMessage();
        Iterable<ClassType> classTypeList = classTypeRepository.findAll();
        String retContent="";
        for(ClassType classType:classTypeList){
            if(retContent.equals("")){
                if(area.equals("en")){
                    retContent = classType.getClassType() + ":" + classType.getTypeDescEn();
                }else {
                    retContent = classType.getClassType() + ":" + classType.getTypeDesc();
                }
            }else{
                if(area.equals("en")){
                    retContent = retContent + "<[CDATA]>" + classType.getClassType() + ":" + classType.getTypeDescEn();
                }else {
                    retContent = retContent + "<[CDATA]>" + classType.getClassType() + ":" + classType.getTypeDesc();
                }
            }
        }
        retMessage.setErrorCode("0");
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,"0"));
        retMessage.setRetContent(retContent);
        return retMessage;
    }


    public RetMessage getClassSubTypeInfo(String area,int classType){
        RetMessage retMessage = new RetMessage();
        List<ClassSubType> classSubTypeList = classSubTypeRepository.findByClassType(classType);
        String retContent="";
        for(ClassSubType classSubType:classSubTypeList){
            if(retContent.equals("")){
                if(area.equals("en")){
                    retContent = classSubType.getClassSubType() + ":" + classSubType.getSubTypeDescEn();
                }else {
                    retContent = classSubType.getClassSubType() + ":" + classSubType.getSubTypeDesc();
                }
            }else{
                if(area.equals("en")){
                    retContent = retContent + "<[CDATA]>" + classSubType.getClassSubType() + ":" + classSubType.getSubTypeDescEn();
                }else {
                    retContent = retContent + "<[CDATA]>" + classSubType.getClassSubType() + ":" + classSubType.getSubTypeDesc();
                }
            }
        }
        retMessage.setErrorCode("0");
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,"0"));
        retMessage.setRetContent(retContent);
        return retMessage;
    }



}