//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.facebook.mojo.alternative;

import com.facebook.swift.alternative.generator.SwiftGeneratorAlternative;
import com.facebook.swift.alternative.generator.SwiftGeneratorConfig;
import com.facebook.swift.alternative.generator.SwiftGeneratorConfig.Builder;
import com.facebook.swift.alternative.generator.SwiftGeneratorTweak;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.*;
import org.apache.maven.scm.provider.git.gitexe.command.checkout.GitCheckOutCommand;
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository;
import org.codehaus.plexus.util.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mojo(
        name = "generate",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES
)
public class SwiftMojoAlternative extends AbstractMojo {
    private static final Pattern scanRequiredPattern = Pattern.compile("^[/\\\\]|[*?]|\\.\\.|[/\\\\]$");
    @Parameter(
            defaultValue = "false"
    )

    private boolean skip = false;
    @Parameter
    private String overridePackage = null;
    @Parameter
    private String defaultPackage = null;

    @Parameter
    private String repoBranch = "master";
    @Parameter(
            required = true
    )
    private FileSet idlFiles = null;
    @Parameter(
            defaultValue = "${project.build.directory}/generated-sources/swift",
            required = true
    )
    private File outputFolder = null;
    @Parameter(
            defaultValue = "false"
    )
    private boolean generateIncludedCode = false;
    @Parameter(
            defaultValue = "true"
    )
    private boolean addThriftExceptions = true;
    @Parameter(
            defaultValue = "false"
    )
    private boolean addCloseableInterface = false;
    @Parameter(
            defaultValue = "true"
    )
    private boolean extendRuntimeException = true;
    @Parameter(
            defaultValue = "java-regular"
    )
    private String codeFlavor = "java-regular";
    @Parameter(
            defaultValue = "false"
    )
    private boolean usePlainJavaNamespace = false;
    @Parameter(
            defaultValue = "${project}",
            required = true,
            readonly = true
    )
    private MavenProject project = null;

    public SwiftMojoAlternative() {
    }

    public final void execute() throws MojoExecutionException, MojoFailureException {
        if(this.skip) {
            this.getLog().info("skip generating swift files");
        } else {
            try {
//                File e = new File(this.project.getBuild().getOutputDirectory());
//                File idlDir = new File(e.getPath() + "/idl/");
                File idlDir = new File(this.idlFiles.getDirectory());
                if(!idlDir.exists()) {
                    idlDir.mkdirs();
                }

//                GitScmProviderRepository repo = new GitScmProviderRepository(this.idlRepo);
//                GitCheckOutCommand cmd = new GitCheckOutCommand();
//                cmd.setLogger(new ScmSystemStreamLog());
//                CommandParameters cmdParams = new CommandParameters();
//                cmdParams.setScmVersion(CommandParameter.SCM_VERSION, new ScmBranch(this.repoBranch));
//                ScmResult scmResult = cmd.execute(repo, new ScmFileSet(idlDir), cmdParams);
//                if(!scmResult.isSuccess()) {
//                    throw new MojoFailureException(scmResult.getCommandOutput());
//                } else {
                    List thriftFiles = this.getFiles(idlDir);
                    Builder configBuilder = SwiftGeneratorConfig.builder().inputBase(idlDir.toURI()).outputFolder(this.outputFolder).overridePackage(this.overridePackage).defaultPackage(this.defaultPackage).generateIncludedCode(this.generateIncludedCode).codeFlavor(this.codeFlavor);
                    if(this.usePlainJavaNamespace) {
                        configBuilder.addTweak(SwiftGeneratorTweak.USE_PLAIN_JAVA_NAMESPACE);
                    }

                    if(this.addThriftExceptions) {
                        configBuilder.addTweak(SwiftGeneratorTweak.ADD_THRIFT_EXCEPTION);
                    }

                    if(this.addCloseableInterface) {
                        configBuilder.addTweak(SwiftGeneratorTweak.ADD_CLOSEABLE_INTERFACE);
                    }

                    if(this.extendRuntimeException) {
                        configBuilder.addTweak(SwiftGeneratorTweak.EXTEND_RUNTIME_EXCEPTION);
                    }

                    final SwiftGeneratorAlternative generator = new SwiftGeneratorAlternative(configBuilder.build());
                    final Function<File, URI> filetoURI = new Function<File, URI>() {
                        @Override
                        public URI apply(final File file) {
                            return file == null ? null : file.toURI();
                        }
                    };
                    generator.parse((Iterable)thriftFiles.stream().map( filetoURI).collect(Collectors.toList()));
//                    generator.parse(Collections2.transform(thriftFiles, URI_TRANSFORMER));

                    this.project.addCompileSourceRoot(this.outputFolder.getPath());
                    idlDir.delete();
//                }
            } catch (Exception var10) {
                Throwables.propagateIfInstanceOf(var10, MojoExecutionException.class);
                Throwables.propagateIfInstanceOf(var10, MojoFailureException.class);
                this.getLog().error(String.format("While executing Mojo %s", new Object[]{this.getClass().getSimpleName()}), var10);
                throw new MojoExecutionException("Failure:", var10);
            }
        }
    }

    private static boolean requiresScan(String pattern) {
        Matcher matcher = scanRequiredPattern.matcher(pattern);
        return matcher.find();
    }

    @VisibleForTesting
    static boolean canBypassScan(List<String> includedFiles, List<String> excludedFiles) {
        return excludedFiles.isEmpty() && !includedFiles.isEmpty() && includedFiles.stream().noneMatch((s) -> {
            return requiresScan(s);
        });
    }

    private List<File> getFiles(File inputFolder) throws IOException {
        return canBypassScan(this.idlFiles.getIncludes(), this.idlFiles.getExcludes())?(List)this.idlFiles.getIncludes().stream().map((s) -> {
            return new File(inputFolder, s);
        }).collect(Collectors.toList()):FileUtils.getFiles(inputFolder, Joiner.on(',').join(this.idlFiles.getIncludes()), Joiner.on(',').join(this.idlFiles.getExcludes()));
    }
}
