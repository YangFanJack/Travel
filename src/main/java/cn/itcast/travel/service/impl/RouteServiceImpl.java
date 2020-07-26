package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.FavoriteDao;
import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.dao.RouteImgDao;
import cn.itcast.travel.dao.SellerDao;
import cn.itcast.travel.dao.impl.FavoriteDaoImpl;
import cn.itcast.travel.dao.impl.RouteDaoImpl;
import cn.itcast.travel.dao.impl.RouteImgDaoImpl;
import cn.itcast.travel.dao.impl.SellerDaoImpl;
import cn.itcast.travel.domain.*;
import cn.itcast.travel.service.RouteService;

import java.util.List;

public class RouteServiceImpl implements RouteService {
    private RouteDao routeDao = new RouteDaoImpl();
    private RouteImgDao routeImgDao = new RouteImgDaoImpl();
    private SellerDao sellerDao = new SellerDaoImpl();
    private FavoriteDao favoriteDao = new FavoriteDaoImpl();

    /**
     * 根据类别进行分类查询
     * @param cid
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Override
    public PageBean<Route> pageQuery(int cid, int currentPage, int pageSize ,String rname) {
        //封装pageBean
        PageBean<Route> pageBean = new PageBean<Route>();
        //这是当前页码
        pageBean.setCurrentPage(currentPage);
        //设置每页显示条数
        pageBean.setPageSize(pageSize);
        //设置总记录数
        int totalCount = routeDao.findTotalCount(cid,rname);
        pageBean.setTotalCount(totalCount);
        //设置当前页的集合
        int start = (currentPage-1)*pageSize;
        List<Route> list = routeDao.findByPage(cid, start, pageSize,rname);
        pageBean.setList(list);
        //设置总页数
        int totalPage = totalCount%pageSize == 0 ? totalCount/pageSize : totalCount/pageSize+1;
        pageBean.setTotalPage(totalPage);

        return pageBean;
    }

    /**
     * 根据id查询
     *
     * @param rid
     * @return
     */
    @Override
    public Route findOne(String rid) {
        //根据id去route表中查询route对象
        Route route = routeDao.findOne(Integer.parseInt(rid));

        //根据route的id查询图片的集合信息
        List<RouteImg> routeImgList = routeImgDao.findByRid(Integer.parseInt(rid));
        //将集合设置到route对象
        route.setRouteImgList(routeImgList);
        //根据route的id查询卖家信息
        Seller seller = sellerDao.findById(route.getSid());
        //将卖家对象设置到route对象
        route.setSeller(seller);

        //收藏次数的查询
        int count = favoriteDao.findCountByRid(route.getRid());
        route.setCount(count);

        return route;
    }
}
