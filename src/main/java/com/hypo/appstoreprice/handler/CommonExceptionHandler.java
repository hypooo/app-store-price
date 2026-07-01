package com.hypo.appstoreprice.handler;

import cn.hutool.core.collection.CollUtil;
import com.hypo.appstoreprice.common.BizException;
import com.hypo.appstoreprice.common.LogUtil;
import com.hypo.appstoreprice.pojo.bean.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;

/**
 * 统一异常处理
 *
 * @author hypo
 * @date 2022-01-08
 */
@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    /**
     * 自定义服务异常
     *
     * @param e e
     * @return {@link R}
     */
    @ExceptionHandler(value = BizException.class)
    public R bizExceptionHandler(BizException e) {
        return R.failed(e.getMessage());
    }

    /**
     * 校验不通过异常处理
     *
     * @param e e
     * @return {@link R}
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        if (CollUtil.isNotEmpty(result.getAllErrors())) {
            return R.failed(result.getAllErrors().get(0).getDefaultMessage());
        }
        return R.failed("参数不正确");
    }

    /**
     * 404：页面请求重定向首页
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView noResourceFoundHandler(NoResourceFoundException ex, HttpServletRequest request) {
        return new ModelAndView("redirect:/");
    }

    /**
     * 系统异常
     *
     * @param e e
     * @return {@link R}
     */
    @ExceptionHandler(value = Exception.class)
    public R exceptionHandler(Exception e) {
        LogUtil.error(log, "系统异常", e);
        return R.failed("系统异常");
    }

    /**
     * 客户端终止异常
     *
     * @param e e
     */
    @SuppressWarnings("all")
    @ExceptionHandler(value = ClientAbortException.class)
    public void clientAbortExceptionHandler(ClientAbortException e) {
        if (e.getCause().getClass().equals(IOException.class)) {
            // 写操作IO异常几乎总是由于客户端主动关闭连接导致，忽略
        } else {
            LogUtil.error(log, "ClientAbortException", e);
        }
    }

}
