package com.xiang.controller;

import com.google.code.kaptcha.Producer;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Slf4j
@Controller
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private Producer producer;

    @SneakyThrows
    @GetMapping("/captcha.jpg")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("image/jpeg");
        String capText = producer.createText();
        log.info("验证码:{}",capText);

        request.getSession().setAttribute("captcha",capText);
        BufferedImage image = producer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();

        ImageIO.write(image,"jpg",out);
        out.flush();

    }

}