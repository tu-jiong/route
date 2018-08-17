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
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by tujiong on 2018/8/15.
 */

@AutoService(Processor.class)
@SupportedOptions("moduleName")
public class RouteProcessor extends AbstractProcessor {

    private String moduleName;
    private Messager messager;
    private Filer filer;
    private Map<String, String> routeMap;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        routeMap = new HashMap<>();

        Map<String, String> options = processingEnv.getOptions();
        if (options != null && !options.isEmpty()) {
            String name = options.get("moduleName");
            if (name != null && name.length() > 0) {
                String c = name.substring(0, 1);
                moduleName = name.replaceFirst(c, c.toUpperCase());
                messager.printMessage(Diagnostic.Kind.NOTE, "module : " + moduleName);
            }
        }

        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Path.class);
        TypeMirror activity = elementUtils.getTypeElement("android.app.Activity").asType();

        if (elements != null) {
            for (Element element : elements) {
                TypeMirror tm = element.asType();
                if (typeUtils.isSubtype(tm, activity)) {
                    TypeElement typeElement = (TypeElement) element;
                    String qualifiedName = typeElement.getQualifiedName().toString();
                    messager.printMessage(Diagnostic.Kind.NOTE, "class : " + qualifiedName);
                    Path annotation = element.getAnnotation(Path.class);
                    routeMap.put(annotation.value(), qualifiedName);
                    brewJava();
                }
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
            messager.printMessage(Diagnostic.Kind.NOTE, "route [" + entry.getKey() + " : " + entry.getValue() + "]");
            ClassName className = ClassName.get("", entry.getValue());
            loadBuilder.addStatement("map.put($S, $T.class)", entry.getKey(), className);
        }
        MethodSpec create = loadBuilder.build();

        MethodSpec get = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC)
                .returns(Map.class)
                .addStatement("return map")
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(moduleName + "Factory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get("com.jm.library", "Factory"))
                .addField(map)
                .addMethod(constructor)
                .addMethod(create)
                .addMethod(get)
                .build();

        JavaFile javaFile = JavaFile.builder("com.jm.route.gen", typeSpec)
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }
}
