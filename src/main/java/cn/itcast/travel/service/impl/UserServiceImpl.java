package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.UuidUtil;

public class UserServiceImpl implements UserService{
    private UserDao userDao = new UserDaoImpl();

    /**
     * 注册用户
     * @param user
     * @return
     */
    @Override
    public boolean regist(User user) {
        //根据用户名查询用户信息
        User u = userDao.findByUsername(user.getUsername());
        //如果已存在则放回false
        if(u != null){
            return false;
        }
        //如果没有该用户保存用户信息
        else{
            //设置激活码，唯一
            user.setCode(UuidUtil.getUuid());
            //设置激活状态
            user.setStatus("N");
            userDao.save(user);
            //注册的时候发送激活邮件
            String content = "<a href = 'http://localhost:8080/Travel/user/active?code="+user.getCode()+"'>点击激活【黑马旅游网】</a>";
            MailUtils.sendMail(user.getEmail(),content,"激活邮件");
            return true;
        }
    }

    /**
     * 激活用户
     * @param code
     * @return
     */
    @Override
    public boolean active(String code) {
        //根据激活码查询用户
        User user = userDao.findByCode(code);
        if(user != null){
            //调用dao的update更新激活状态
            userDao.updateStatus(user);
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * 用户登录
     * @param user
     * @return
     */
    @Override
    public User login(User user) {
        return userDao.findByUsernameAndPassword(user.getUsername(),user.getPassword());
    }
}
