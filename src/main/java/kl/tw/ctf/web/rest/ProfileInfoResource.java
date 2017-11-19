package kl.tw.ctf.web.rest;

import kl.tw.ctf.config.DefaultProfileUtil;

import io.github.jhipster.config.JHipsterProperties;

import kl.tw.ctf.dao.DataFile;
import kl.tw.ctf.service.impl.DataFileParserServiceImpl;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Resource to return information about the currently running Spring profiles.
 */
@RestController
@RequestMapping("/api")
public class ProfileInfoResource {

    private final Environment env;

    private final JHipsterProperties jHipsterProperties;

    public ProfileInfoResource(Environment env, JHipsterProperties jHipsterProperties) {
        this.env = env;
        this.jHipsterProperties = jHipsterProperties;
    }

    @GetMapping("/profile-info")
    public ProfileInfoVM getActiveProfiles() {
        String[] activeProfiles = DefaultProfileUtil.getActiveProfiles(env);
        return new ProfileInfoVM(activeProfiles, getRibbonEnv(activeProfiles));
    }

    @GetMapping("/context-info")
    public ContextInfoVm getContextInfo() {
        String currentContext = DataFileParserServiceImpl.currentlyOpenFile != null ? DataFileParserServiceImpl.currentlyOpenFile.getTableName() : null;
        return new ContextInfoVm(currentContext, null, null, null);
    }


    private String getRibbonEnv(String[] activeProfiles) {
        String[] displayOnActiveProfiles = jHipsterProperties.getRibbon().getDisplayOnActiveProfiles();
        if (displayOnActiveProfiles == null) {
            return null;
        }
        List<String> ribbonProfiles = new ArrayList<>(Arrays.asList(displayOnActiveProfiles));
        List<String> springBootProfiles = Arrays.asList(activeProfiles);
        ribbonProfiles.retainAll(springBootProfiles);
        if (!ribbonProfiles.isEmpty()) {
            return ribbonProfiles.get(0);
        }
        return null;
    }

    class ProfileInfoVM {

        private String[] activeProfiles;

        private String ribbonEnv;

        ProfileInfoVM(String[] activeProfiles, String ribbonEnv) {
            this.activeProfiles = activeProfiles;
            this.ribbonEnv = ribbonEnv;
        }

        public String[] getActiveProfiles() {
            return activeProfiles;
        }

        public String getRibbonEnv() {
            return ribbonEnv;
        }
    }

    class ContextInfoVm {
        private String currentContext;
        private String[] contexts;
        private String currentDataSet;
        private String[] dataSet;

        public String getCurrentContext() {
            return currentContext;
        }

        public String[] getContexts() {
            return contexts;
        }

        public String getCurrentDataSet() {
            return currentDataSet;
        }

        public String[] getDataSet() {
            return dataSet;
        }

        public ContextInfoVm(String currentContext, String[] contexts, String currentDataSet, String[] dataSet) {
            this.currentContext = currentContext;
            this.contexts = contexts;
            this.currentDataSet = currentDataSet;
            this.dataSet = dataSet;
        }

    }
}
