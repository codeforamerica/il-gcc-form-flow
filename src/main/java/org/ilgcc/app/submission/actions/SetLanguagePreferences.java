package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetLanguagePreferences implements Action {

    public static final Map<String, String> LOCALE_TO_LANG = Map.of("es", "Spanish", "en", "English");

    @Override
    public void run(Submission submission) {
        Locale locale = LocaleContextHolder.getLocale();

        String languageToSet = LOCALE_TO_LANG.getOrDefault(locale.getLanguage(), "English");
        submission.getInputData().putIfAbsent("languageRead", languageToSet);
        submission.getInputData().putIfAbsent("languageSpeak", languageToSet);
    }
}
