package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.FavoriteService;
import cn.itcast.travel.service.RouteService;
import cn.itcast.travel.service.impl.FavoriteServiceImpl;
import cn.itcast.travel.service.impl.RouteServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/route/*")
public class RouteServlet extends BaseServlet {
    private RouteService routeService = new RouteServiceImpl();
    private FavoriteService favoriteService = new FavoriteServiceImpl();

    /**
     * 分页查询
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void pageQuery(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //接受参数
        String currentPageStr = request.getParameter("currentPage");
        String pageSizeStr = request.getParameter("pageSize");
        String cidStr = request.getParameter("cid");

        //接受搜索的线路名称
        String rname = request.getParameter("rname");


        //处理参数
        int cid = 0;//类别id，下面sql用
        int currentPage = 0;//当前页码，如果不传默认1
        int pageSize = 0;//当页显示条数，如果不传默认5
        if(cidStr != null && cidStr.length()>0 && !"null".equals(cidStr) ){
            cid = Integer.parseInt(cidStr);
        }
        if(currentPageStr != null && currentPageStr.length()>0){
            currentPage = Integer.parseInt(currentPageStr);
        }
        else {
            currentPage=1;
        }
        if(pageSizeStr != null && pageSizeStr.length()>0 ){
            pageSize = Integer.parseInt(pageSizeStr);
        }
        else {
            pageSize=5;
        }

        //调用service查询pageBean对象
        PageBean<Route> routePageBean = routeService.pageQuery(cid, currentPage, pageSize,rname);
        //将pageBean对象序列化为json返回
        myWriteValue(routePageBean,response);
    }

    /**
     * 根据id查询一个route详细信息
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //接受id
        String rid = request.getParameter("rid");
        //调用service查询route对象
        Route route = routeService.findOne(rid);
        //转为json协会客户端
        myWriteValue(route,response);

    }

    /**
     * 判断当前登录用户是否收藏过改线路
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void isFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String rid = request.getParameter("rid");
        User user = (User) request.getSession().getAttribute("user");
        int uid;
        if(user == null){
            //未登录
            uid = 0;
        }
        else{
            //已登录
            uid = user.getUid();
        }
        //调用service查询是否收藏
        boolean isFavorite = favoriteService.isFavorite(rid, uid);
        myWriteValue(isFavorite,response);
    }

    /**
     * 添加收藏
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void addFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取rid和uid
        String rid = request.getParameter("rid");
        User user = (User) request.getSession().getAttribute("user");
        int uid;
        if(user == null){
            //未登录
            return;
        }
        else{
            //已登录
            uid = user.getUid();
        }
        //调用service添加收藏
        favoriteService.add(rid,uid);
    }

}
