package com.mercari.processor;


import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

public class ExperimentListWriter {
    ExperimentListModel model;

    public ExperimentListWriter(ExperimentListModel model) {
        this.model = model;
    }

    public void writer(Filer filer) throws IOException {
        TypeSpec.Builder outEnumBuilder = TypeSpec.enumBuilder(model.getClassName().simpleName());
        outEnumBuilder.addModifiers(Modifier.PUBLIC);
        HashMap<String, String> experimentsHashMap = model.getExperimentsHashMap();
        Iterator entries = experimentsHashMap.entrySet().iterator();
        while (entries.hasNext()){
            Map.Entry entry = (Map.Entry) entries.next();
            outEnumBuilder
                    .addEnumConstant((String) entry.getKey(),
                            TypeSpec.anonymousClassBuilder("$S",(String)entry.getValue()).build());
        }
        outEnumBuilder.addField(String.class, "testName", Modifier.PRIVATE, Modifier.FINAL)
                .addMethods(createEnumMethods());

        TypeSpec outEnum = outEnumBuilder.build();
        JavaFile.builder(model.getClassName().packageName(), outEnum)
                .build()
                .writeTo(filer);

        TypeSpec.Builder outAnnotationBuilder = TypeSpec.annotationBuilder("ForExperiment");
        outAnnotationBuilder
                .addModifiers(Modifier.PUBLIC)
                .addAnnotations(createAnnotations())
                .addMethod(createAnnotationMethod());

        TypeSpec outAnnotation = outAnnotationBuilder.build();

        JavaFile.builder(model.getClassName().packageName(), outAnnotation)
                .build()
                .writeTo(filer);

    }

    private List<MethodSpec> createEnumMethods(){
        List<MethodSpec> methodSpecs = new ArrayList<>();
        methodSpecs.add(createConstructor());
        methodSpecs.add(createGetter());
        methodSpecs.add(createGetParams());
        return methodSpecs;
    }

    private MethodSpec createConstructor(){
        return MethodSpec.constructorBuilder()
                .addParameter(String.class, "testName")
                .addStatement("this.$N = $N", "testName", "testName")
                .build();
    }

    private MethodSpec createGetter(){
        return MethodSpec.methodBuilder("getTestName")
                .returns(String.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return this.testName").build();
    }

    private MethodSpec createGetParams(){
        return MethodSpec.methodBuilder("getTestNameParams")
                .returns(String.class)
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .addStatement("StringBuilder builder = new StringBuilder()")
                .beginControlFlow("for (" + model.getClassName().simpleName() + " list : values())")
                .addStatement("builder.append(list.getTestName())")
                .addStatement("builder.append(\",\")")
                .endControlFlow()
                .addStatement("builder.deleteCharAt(builder.length()-1)")
                .addStatement("return builder.toString()")
                .build();
    }

    private List<AnnotationSpec> createAnnotations() {
        List<AnnotationSpec> list = new ArrayList<>();
        list.add(AnnotationSpec.builder(Retention.class)
                .addMember("value", "$L", "java.lang.annotation.RetentionPolicy.RUNTIME")
                .build());
        list.add(AnnotationSpec.builder(Target.class)
                .addMember("value", "$L", "java.lang.annotation.ElementType.FIELD")
                .addMember("value", "$L", "java.lang.annotation.ElementType.TYPE")
                .addMember("value", "$L", "java.lang.annotation.ElementType.LOCAL_VARIABLE")
                .addMember("value", "$L", "java.lang.annotation.ElementType.METHOD")
                .build());
        return list;
    }

    private MethodSpec createAnnotationMethod() {
        return MethodSpec.methodBuilder("value")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ClassName.get(model.getClassName().packageName(), model.getClassName().simpleName()))
                .build();
    }
}
