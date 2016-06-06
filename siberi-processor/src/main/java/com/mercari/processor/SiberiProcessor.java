package com.mercari.processor;

import com.google.auto.service.AutoService;
import com.mercari.siberi.annotations.ExperimentalList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class SiberiProcessor extends AbstractProcessor{
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ExperimentalList.class.getName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        this.elementUtils = env.getElementUtils();
        this.filer = env.getFiler();
        this.messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<ExperimentListModel> list = parseExperimentListModel(roundEnv, elementUtils);
        for (ExperimentListModel model : list) {
            ExperimentListWriter writer = new ExperimentListWriter(model);
            try {
                writer.writer(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }
        return true;
    }

    private List<ExperimentListModel> parseExperimentListModel(RoundEnvironment roundEnv, Elements elementUtils) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ExperimentalList.class);
        List<ExperimentListModel> list = new ArrayList<>();
        for (Element element : elements) {
            list.add(new ExperimentListModel((TypeElement) element, elementUtils));
        }
        return list;
    }
}
