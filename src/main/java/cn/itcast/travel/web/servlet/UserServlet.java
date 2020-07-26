package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    //抽取出Userservice的业务逻辑处理对象
    private UserService service = new UserServiceImpl();

    /**
     * 注册功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //验证码校验
        String check = request.getParameter("check");
        String checkcode_server = (String) request.getSession().getAttribute("CHECKCODE_SERVER");

        //防止返回时验证码仍然有效
        request.getSession().removeAttribute("CHECKCODE_SERVER");
        if(checkcode_server == null || !check.equalsIgnoreCase(checkcode_server)){
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("验证码错误!");
            //将resultInfo序列化为json对象
//            ObjectMapper mapper = new ObjectMapper();
//            String json = mapper.writeValueAsString(resultInfo);
//            response.setContentType("application/json;charset=utf-8");
//            response.getWriter().write(json);
            myWriteValueAsString(resultInfo,response);
            return;
        }


        //获取数据
        Map<String, String[]> parameterMap = request.getParameterMap();
        //封装对象
        User user = new User();
        try {
            BeanUtils.populate(user,parameterMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //调用service
        //UserService userService = new UserServiceImpl();
        boolean flag = service.regist(user);
        //响应结果
        ResultInfo resultInfo = new ResultInfo();
        if(flag) {
            resultInfo.setFlag(true);
        }
        else {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("注册失败,用户已存在!");
        }
        //将resultInfo序列化为json对象
//        ObjectMapper mapper = new ObjectMapper();
//        String json = mapper.writeValueAsString(resultInfo);
//        response.setContentType("application/json;charset=utf-8");
//        response.getWriter().write(json);
        myWriteValueAsString(resultInfo,response);
    }

    /**
     * 登录功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取用户名和密码
        Map<String, String[]> map = request.getParameterMap();
        //封装
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //调用service查询
//        UserService service = new UserServiceImpl();
        User u = service.login(user);
        //判断用户是否存在
        ResultInfo resultInfo = new ResultInfo();
        if(u == null){
            //用户名或者密码错误
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("用户名或密码错误!");
        }
        //判断用户是否激活
        else{
            if(!"Y".equals(u.getStatus())){
                //尚未激活
                resultInfo.setFlag(false);
                resultInfo.setErrorMsg("您尚未激活,请前往邮箱激活!");
            }
            else{
                request.getSession().setAttribute("user",u);
                resultInfo.setFlag(true);
            }
        }
        //响应数据
//        ObjectMapper objectMapper = new ObjectMapper();
//        response.setContentType("application/json;charset=utf-8");
//        objectMapper.writeValue(response.getOutputStream(),resultInfo);
        myWriteValue(resultInfo,response);
    }

    /**
     * 查询单个用户
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //从session中获取登录用户
        Object user = request.getSession().getAttribute("user");
//        ObjectMapper objectMapper = new ObjectMapper();
//        response.setContentType("application/json;charset=utf-8");
//        objectMapper.writeValue(response.getOutputStream(),user);
        myWriteValue(user,response);
    }

    /**
     * 退出功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void exit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //销魂session
        request.getSession().invalidate();
        //跳转页面
//        response.sendRedirect("login.html");
        response.sendRedirect(request.getContextPath()+"/login.html");
    }

    /**
     * 激活功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        if(code != null){
            //调用service完成激活
//            UserService service = new UserServiceImpl();
            boolean flag = service.active(code);
            //判断标记
            String msg = null;
            if(flag){
                //激活成功
                msg = "激活成功，请<a href='login.html'>登录</a>";
            }
            else{
                //激活失败
                msg = "激活成功，请联系管理员!";
            }
//            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }
}
