package com.jm.complier;

import com.google.auto.service.AutoService;
import com.jm.annotation.Path;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by tujiong on 2018/8/15.
 */

@AutoService(Processor.class)
public class RouteProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Filer filer;
    private Map<String, String> routeMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        routeMap = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, "process()");

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Path.class);

        if (elements != null) {
            for (Element element : elements) {
                Path annotation = element.getAnnotation(Path.class);

                routeMap.put(annotation.value(), element.toString());

                brewJava();

            }
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(com.jm.annotation.Path.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void brewJava() {
        FieldSpec map = FieldSpec.builder(Map.class, "map", Modifier.PRIVATE)
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("map =new $T<String,Class>()", HashMap.class)
                .build();

        MethodSpec.Builder loadBuilder = MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);

        for (Map.Entry<String, String> entry : routeMap.entrySet()) {
            messager.printMessage(Diagnostic.Kind.NOTE, entry.getKey() + "   " + entry.getValue());
            ClassName className = ClassName.get("com.jm.route", entry.getValue());
            loadBuilder.addStatement("map.put($S, $T.class)", entry.getKey(), className);
        }
        MethodSpec create = loadBuilder.build();

        MethodSpec get = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC)
                .returns(Map.class)
                .addStatement("return map")
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder("DefaultFactory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get("com.jm.library", "Factory"))
                .addField(map)
                .addMethod(constructor)
                .addMethod(create)
                .addMethod(get)
                .build();

        JavaFile javaFile = JavaFile.builder("com.jm.route", typeSpec)
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }
}
