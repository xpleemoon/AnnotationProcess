package com.xpleemoon.compiler;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.xpleemoon.annotations.InjectView;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

final class ViewBinding extends AbstractBinding<InjectView> {

    ViewBinding(Class<InjectView> clz, Elements elementUtils, Messager messager, List<Element> elementList) {
        super(clz, elementUtils, messager, elementList);
    }

    @Override
    String getMethodName() {
        return "bindView";
    }

    @Override
    MethodSpec generateMethod() {
        super.generateMethod();

        TypeName enclosingTypeName = TypeInfoUtils.getEnclosingTypeName(mElementList.get(0)); // 获取注解宿主类
        MethodSpec.Builder builder = MethodSpec.methodBuilder(getMethodName())
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(void.class)
                .addParameter(enclosingTypeName, "target")
                .addJavadoc("为{@code $N}中{@link $T}注解的view变量绑定数据", "target", mClz);
        for (Element viewInjectorElement : mElementList) {
            TypeName typeName = TypeInfoUtils.getTypeName(viewInjectorElement); // 获取注解修饰的字段类型
            int viewId = viewInjectorElement.getAnnotation(mClz).id(); // 获取注解指定的id值
            builder.addStatement("target.$N = ($T) target.findViewById($L)", viewInjectorElement.getSimpleName(), typeName, viewId); // 构建赋值语句
        }
        return builder.build();
    }
}
