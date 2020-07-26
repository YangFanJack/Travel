package cn.itcast.travel.web.servlet.store;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        UserService service = new UserServiceImpl();
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
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        objectMapper.writeValue(response.getOutputStream(),resultInfo);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }
}
