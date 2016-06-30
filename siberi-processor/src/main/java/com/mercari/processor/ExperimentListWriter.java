package com.mercari.processor;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
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
        TypeSpec.Builder outClassBuilder = TypeSpec.classBuilder(model.getClassName().simpleName());
        outClassBuilder.addModifiers(Modifier.PUBLIC);
        HashMap<String, String> experimentsHashMap = model.getExperimentsHashMap();

        outClassBuilder
                .addFields(createField(experimentsHashMap))
                .addMethod(createGetParams(experimentsHashMap));

        TypeSpec outClass = outClassBuilder.build();
        JavaFile.builder(model.getClassName().packageName(), outClass)
                .build()
                .writeTo(filer);
    }

    private List<FieldSpec> createField(HashMap<String,String> experimentsHashMap){
        Iterator entries = experimentsHashMap.entrySet().iterator();
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        while (entries.hasNext()){
            Map.Entry entry = (Map.Entry) entries.next();
            fieldSpecs.add(
                    FieldSpec
                            .builder(String.class, (String) entry.getKey())
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("$S", (String) entry.getValue())
                            .build());
        }
        return fieldSpecs;
    }

    private MethodSpec createGetParams(HashMap<String,String> experimentsHashMap){
        Iterator entries = experimentsHashMap.entrySet().iterator();
        ClassName textUtils = ClassName.get("android.text", "TextUtils");
        MethodSpec.Builder method = MethodSpec.methodBuilder("getTestNameParams");
        method.returns(String.class)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        StringBuilder builder = new StringBuilder("String params[] = {");
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            builder.append((String) entry.getKey());
            if (entries.hasNext()) {
                builder.append(",");
            }
        }
        builder.append("}");
        method.addStatement(builder.toString())
                .addStatement("return $T.join(\",\", params)",textUtils);
        return method.build();
    }
}
