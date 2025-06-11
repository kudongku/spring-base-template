package kr.soulware.teen_mydata.service;

import kr.soulware.teen_mydata.dto.request.LoggingData;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final Environment env;

    @Async
    public void sendErrorMailAsync(LoggingData data) {
        String subject = createErrorMailSubject(data);
        String text = createErrorMailContent(data);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo(env.getProperty("notification.email"));
            messageHelper.setSubject(subject);
            messageHelper.setText(text, true);
        };

        mailSender.send(messagePreparator);
    }

    private String createErrorMailSubject(LoggingData data) {
        return String.format("마이데이터 조성사업%s 500번대 에러 발생: %s",
            Arrays.toString(env.getActiveProfiles()),
            data.getRequestUri());
    }

    private String createErrorMailContent(LoggingData data) {
        return String.format("""
                <h2>500번대 에러 발생</h2>
                <p><strong>요청 URI:</strong> %s</p>
                <p><strong>Method:</strong> %s</p>
                <p><strong>Status:</strong> %d</p>
                <p><strong>RequestBody:</strong> <pre>%s</pre></p>
                <p><strong>ResponseBody:</strong> <pre>%s</pre></p>
                """,
            data.getRequestUri(),
            data.getHttpMethod(),
            data.getStatus(),
            data.getRequestBody(),
            data.getResponseBody());
    }

}