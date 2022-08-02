package cn.silver.framework.web.controller.tool;

import cn.silver.framework.mq.po.Mail;
import cn.silver.framework.mq.po.TopicMail;
import cn.silver.framework.mq.service.IProducerService;
import cn.silver.framework.mq.service.Publisher;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author Administrator
 */
@RestController
@Api(tags = "消息中间件测试")
@RequestMapping("test/mq")
public class RabbitMQController {
    @Autowired
    IProducerService producer;

    @Autowired
    Publisher publisher;

    @ApiOperation(value = "消息推送")
    @PostMapping(value = "/produce", produces = {"application/json;charset=UTF-8"})
    public void produce(@RequestBody Mail mail) throws Exception {
        producer.sendMail("gacim.direct.queue1", mail);
    }

    @GetMapping(value = "/topic", produces = {"application/json;charset=UTF-8"})
    public void topic(@ModelAttribute("mail") Mail mail) throws Exception {
        publisher.publishMail(mail);
    }

    @GetMapping(value = "/direct", produces = {"application/json;charset=UTF-8"})
    public void direct(@ModelAttribute("mail") TopicMail mail) {
        Mail m = new Mail(mail.getMailId(), mail.getCountry(), mail.getWeight());
        publisher.senddirectMail(m, mail.getRoutingkey());
    }

    @GetMapping(value = "/mytopic", produces = {"application/json;charset=UTF-8"})
    public void topic(@ModelAttribute("mail") TopicMail mail) {
        Mail m = new Mail(mail.getMailId(), mail.getCountry(), mail.getWeight());
        publisher.sendtopicMail(m, mail.getRoutingkey());
    }
}
