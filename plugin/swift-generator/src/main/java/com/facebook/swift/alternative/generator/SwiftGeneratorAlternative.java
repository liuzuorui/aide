//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.facebook.swift.alternative.generator;

import com.facebook.swift.alternative.generator.util.SwiftInternalStringUtils;
import com.facebook.swift.alternative.generator.util.TemplateLoader;
import com.facebook.swift.alternative.generator.visitors.ConstantsVisitor;
import com.facebook.swift.alternative.generator.visitors.ExceptionVisitor;
import com.facebook.swift.alternative.generator.visitors.IntegerEnumVisitor;
import com.facebook.swift.alternative.generator.visitors.ServiceVisitor;
import com.facebook.swift.alternative.generator.visitors.StringEnumVisitor;
import com.facebook.swift.alternative.generator.visitors.StructVisitor;
import com.facebook.swift.alternative.generator.visitors.TypeVisitor;
import com.facebook.swift.alternative.generator.visitors.UnionVisitor;
import com.facebook.swift.parser.model.Document;
import com.facebook.swift.parser.model.Header;
import com.facebook.swift.parser.visitor.DocumentVisitor;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.airlift.log.Logger;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.annotation.Nullable;

public class SwiftGeneratorAlternative {
    private static final Logger LOG = Logger.get(SwiftGeneratorAlternative.class);
    private static final Map<String, ImmutableList<String>> TEMPLATES = ImmutableMap.of("java-regular", ImmutableList.of("java/common.st", "java/regular.st"), "java-immutable", ImmutableList.of("java/common.st", "java/immutable.st"), "java-ctor-no-camelcase", ImmutableList.of("java/common.st", "java/ctor_no_camelcase.st"), "java-ctor", ImmutableList.of("java/common.st", "java/ctor.st"));
    private final File outputFolder;
    private final SwiftGeneratorConfig swiftGeneratorConfig;
    private final TemplateLoader templateLoader;
    private final Set<URI> parsedDocuments = new HashSet();
    private final Stack<URI> parentDocuments = new Stack();

    public SwiftGeneratorAlternative(SwiftGeneratorConfig swiftGeneratorConfig) {
        Preconditions.checkState(TEMPLATES.get(swiftGeneratorConfig.getCodeFlavor()) != null, "Templating type %s is unknown!", new Object[]{swiftGeneratorConfig.getCodeFlavor()});
        this.swiftGeneratorConfig = swiftGeneratorConfig;
        this.outputFolder = swiftGeneratorConfig.getOutputFolder();
        if(this.outputFolder != null) {
            this.outputFolder.mkdirs();
        }

        LOG.debug("Writing source files into %s using %s ...", new Object[]{this.outputFolder, swiftGeneratorConfig.getCodeFlavor()});
        this.templateLoader = new TemplateLoader((Iterable)TEMPLATES.get(swiftGeneratorConfig.getCodeFlavor()));
    }

    public void parse(Iterable<URI> inputs) throws Exception {
        Preconditions.checkArgument(inputs != null && inputs.iterator().hasNext(), "No input files!");
        LOG.info("Parsing Thrift IDL from %s...", new Object[]{inputs});
        HashMap contexts = Maps.newHashMap();
        Iterator var3 = inputs.iterator();

        while(var3.hasNext()) {
            URI context = (URI)var3.next();
            this.parsedDocuments.clear();
            this.parseDocument(context.isAbsolute()?context:this.swiftGeneratorConfig.getInputBase().resolve(context), contexts, new TypeRegistry(), new TypedefRegistry());
        }

        LOG.info("IDL parsing complete, writing java code...");
        var3 = contexts.values().iterator();

        while(var3.hasNext()) {
            SwiftDocumentContext context1 = (SwiftDocumentContext)var3.next();
            this.generateFiles(context1);
        }

        LOG.info("Java code generation complete.");
    }

    private void parseDocument(URI thriftUri, @Nullable Map<String, SwiftDocumentContext> contexts, TypeRegistry typeRegistry, TypedefRegistry typedefRegistry) throws IOException {
        Preconditions.checkState(thriftUri != null && thriftUri.isAbsolute() && !thriftUri.isOpaque(), "Only absolute, non opaque URIs can be parsed!");
        Preconditions.checkArgument(!this.parentDocuments.contains(thriftUri), "Input %s recursively includes itself (%s)", new Object[]{thriftUri, Joiner.on(" -> ").join(this.parentDocuments) + " -> " + thriftUri});
        if(this.parsedDocuments.contains(thriftUri)) {
            LOG.debug("Skipping already parsed file %s...", new Object[]{thriftUri});
        } else {
            LOG.debug("Parsing %s...", new Object[]{thriftUri});
            String thriftNamespace = this.extractThriftNamespace(thriftUri);
            Preconditions.checkState(!SwiftInternalStringUtils.isBlank(thriftNamespace), "Thrift URI %s can not be translated to a namespace", new Object[]{thriftUri});
            SwiftDocumentContext context = new SwiftDocumentContext(thriftUri, thriftNamespace, this.swiftGeneratorConfig, typeRegistry, typedefRegistry);
            Document document = context.getDocument();
            Header header = document.getHeader();
            String javaPackage = context.getJavaPackage();
            typeRegistry.add(new SwiftJavaType(thriftNamespace, "Constants", "Constants", javaPackage));
            this.parentDocuments.push(thriftUri);

            try {
                Iterator var10 = header.getIncludes().iterator();

                while(var10.hasNext()) {
                    String include = (String)var10.next();
                    URI includeUri = this.swiftGeneratorConfig.getInputBase().resolve(include);
                    LOG.debug("Found %s included from %s.", new Object[]{includeUri, thriftUri});
                    this.parseDocument(includeUri, this.swiftGeneratorConfig.isGenerateIncludedCode()?contexts:null, typeRegistry, typedefRegistry);
                }
            } finally {
                this.parentDocuments.pop();
            }

            this.parsedDocuments.add(thriftUri);
            document.visit(new TypeVisitor(javaPackage, context));
            if(contexts != null && contexts.put(context.getNamespace(), context) != null) {
                LOG.info("Thrift Namespace %s included multiple times!", new Object[]{context.getNamespace()});
            }

        }
    }

    private String extractThriftNamespace(URI thriftUri) {
        String path = thriftUri.getPath();
        String filename = (String)Iterables.getLast(Splitter.on('/').split(path), (Object)null);
        Preconditions.checkState(filename != null, "No thrift namespace found in %s", new Object[]{thriftUri});
        String name = (String)Iterables.getFirst(Splitter.on('.').split(filename), (Object)null);
        Preconditions.checkState(name != null, "No thrift namespace found in %s", new Object[]{thriftUri});
        return name;
    }

    private void generateFiles(SwiftDocumentContext context) throws IOException {
        LOG.debug("Generating code for %s...", new Object[]{context.getNamespace()});
        Preconditions.checkState(this.outputFolder != null, "The output folder was not set!");
        Preconditions.checkState(this.outputFolder.isDirectory() && this.outputFolder.canWrite() && this.outputFolder.canExecute(), "output folder \'%s\' is not valid!", new Object[]{this.outputFolder.getAbsolutePath()});
        ArrayList visitors = Lists.newArrayList();
        visitors.add(new ServiceVisitor(this.templateLoader, context, this.swiftGeneratorConfig, this.outputFolder));
        visitors.add(new StructVisitor(this.templateLoader, context, this.swiftGeneratorConfig, this.outputFolder));
        visitors.add(new UnionVisitor(this.templateLoader, context, this.swiftGeneratorConfig, this.outputFolder));
        visitors.add(new ExceptionVisitor(this.templateLoader, context, this.swiftGeneratorConfig, this.outputFolder));
        visitors.add(new IntegerEnumVisitor(this.templateLoader, context, this.swiftGeneratorConfig, this.outputFolder));
        visitors.add(new StringEnumVisitor(this.templateLoader, context, this.swiftGeneratorConfig, this.outputFolder));
        visitors.add(new ConstantsVisitor(this.templateLoader, context, this.swiftGeneratorConfig, this.outputFolder));
        Iterator var3 = visitors.iterator();

        while(var3.hasNext()) {
            DocumentVisitor visitor = (DocumentVisitor)var3.next();
            context.getDocument().visit(visitor);
            visitor.finish();
        }

    }
}
