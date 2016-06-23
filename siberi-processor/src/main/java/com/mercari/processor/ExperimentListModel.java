package com.mercari.processor;


import com.squareup.javapoet.ClassName;

import java.util.HashMap;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import static javax.lang.model.util.ElementFilter.fieldsIn;

public class ExperimentListModel {
    private ClassName className;
    private HashMap<String, String> experimentsHashMap = new HashMap<>();

    public ClassName getClassName(){
        return className;
    }

    public HashMap<String, String> getExperimentsHashMap() {
        return experimentsHashMap;
    }

    public ExperimentListModel(TypeElement element, Elements elementUtils){
        String packageName = getPackageName(elementUtils, element);
        String originalClassName = getClassName(element, packageName);
        this.className = ClassName.get(packageName,originalClassName + "Util");
        setExperimentsName(element);
    }

    private String getPackageName(Elements elementUtils, TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private static String getClassName(TypeElement element, String packageName) {
        int packageLen = packageName.length() + 1;
        return element.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    private void setExperimentsName(Element element) {
        List<VariableElement> fs = fieldsIn(element.getEnclosedElements());
        for (VariableElement variableElement : fs) {
            experimentsHashMap.put(variableElement.getSimpleName().toString(), (String) variableElement.getConstantValue());
        }
    }
}
