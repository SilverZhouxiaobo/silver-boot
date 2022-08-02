package cn.silver.framework.web.controller.common;

import cn.silver.framework.common.constant.Constants;
import cn.silver.framework.common.utils.id.IdUtils;
import cn.silver.framework.common.utils.sign.Base64;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.redis.RedisCache;
import cn.silver.framework.system.dto.system.CaptchaImageDTO;
import cn.silver.framework.system.service.ISysConfigService;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 *
 * @author hb
 */
@RestController
@Api(tags = {"【验证码管理】"})
public class CaptchaController {
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    // 验证码类型
    @Value("${app.captchaType}")
    private String captchaType;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysConfigService configService;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    @ApiOperation("生成验证码")
    public Response<CaptchaImageDTO> getCode() {
        CaptchaImageDTO captchaImageDTO = new CaptchaImageDTO();
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        captchaImageDTO.setCaptchaOnOff(captchaOnOff);
        if (!captchaOnOff) {
            return Response.success(captchaImageDTO);
        }
        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String capStr, code = null;
        BufferedImage image = null;
        // 生成验证码
        if ("math".equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else if ("char".equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }
        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return Response.error(e.getMessage());
        }
        captchaImageDTO.setUuid(uuid);
        captchaImageDTO.setImg(Base64.encode(os.toByteArray()));
        return Response.success(captchaImageDTO);
    }
}
