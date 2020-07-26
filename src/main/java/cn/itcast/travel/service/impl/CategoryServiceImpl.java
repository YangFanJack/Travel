package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.dao.impl.CategoryDaoImpl;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDao categoryDao = new CategoryDaoImpl();

    /**
     * 查询所有对象
     * @return
     */
    @Override
    public List<Category> findAll() {
        //从redis中查询
        Jedis jedis = JedisUtil.getJedis();
        //期望数据库中的顺序就是展示的顺序，所以使用sortedset排序查询
        //Set<String> categories = jedis.zrange("category", 0, -1);

        //查询sortedset中的分数和值
        Set<Tuple> categories = jedis.zrangeWithScores("category", 0, -1);

        //判断集合是否为空
        List<Category> list = null;
        //为空，需要查询数据库
        if(categories == null || categories.size()==0){
            System.out.println("从数据库查询......");
            list = categoryDao.findAll();
            //存入redis
            for(int i = 0;i<list.size();i++){
                jedis.zadd("category",list.get(i).getCid(),list.get(i).getCname());
            }
        }
        //不为空，将set存入list
        else {
            System.out.println("从缓存查询......");
            list = new ArrayList<Category>();
            for(Tuple tuple : categories){
                Category category = new Category();
                category.setCname(tuple.getElement());
                category.setCid((int) tuple.getScore());
                list.add(category);
            }
        }
        return list;
    }
}
