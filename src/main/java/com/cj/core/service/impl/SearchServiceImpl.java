package com.cj.core.service.impl;

import com.cj.core.pojo.SearchResult;
import com.cj.core.service.SearchMapper;
import com.cj.core.service.SearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 搜索服务的service
 * 2018/5/17
 *
 * @author cj
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchMapper searchDao;

    /**
     * 调用dao层,根据查询条件,从solr服务获取对应的分页数据.
     * dao层是直接从solr服务拿的数据,而非db.
     *
     * @param queryString 查询条件
     * @param page        当前页码
     * @param rows        每页显示多少条记录
     * @return SearchResult
     * @throws Exception Exception
     */
    @Override
    public SearchResult search(String queryString, int page, int rows) throws Exception {
        //创建查询条件
        SolrQuery query = new SolrQuery();  //该对象在上面的dao中出现过.就是Solr的查询条件的实体.
        //设置查询条件
        //如果queryString为空,就按空去查.
        //形参, 也就是要查询的关键字了!
        query.setQuery(queryString);
        //设置分页条件
        //起始行 注意,分页数据是在这里传给Solr的,是物理分页没错.
        query.setStart((page - 1) * rows);
        query.setRows(rows);//每页显示多少行.
        //设置默认搜索域
        //参数一:默认搜索与固定的名字.对应Solr后台页面的"df".
        //参数二:自己指定的搜索域. 京东都是搜索的商品标题.
        query.set("df", "item_title");
        //设置高亮
        //打开高亮.(相当于后台页面中hl打钩)
        //只有在这里设置了这个属性,函数栈下层才可以拿到"highlighting"节点的值.
        query.setHighlight(true);
        //高亮显示的域.
        query.addHighlightField("item_title");
        //高亮html的前缀.
        query.setHighlightSimplePre("<font class=\"skcolor_ljg\">");
        //后缀.
        query.setHighlightSimplePost("</font>");

        //调用dao层,执行查询
        SearchResult searchResult = searchDao.search(query);  //此方法在上面的daoImpl中实现的.

        /**
         SearchResult这个pojo的字段:
            private List<SearchItem> itemList; //查询的结果集-即商品列表. 在dao层封装的.
            private Long recordCount; //查询结果总记录数.  在dao层封装的.
            private int pageCount; //查询结果的总页数.  要在这里封装.
            private int curPage; //当前页码.  要在这里封装.
        */

        //计算总页数...
        //先用SearchResult自带的方法,取出返回结果的总记录数.
        Long recordCount = searchResult.getRecordCount();
        //总页数=总记录数除以每页显示的条数(形参).
        int pageCount = (int) (recordCount / rows);
        //别忘记取余. 如果不能整除,还得+1.
        if (recordCount % rows > 0) {
            pageCount++;
        }
        //把总记录数,设置进searchResult
        searchResult.setPageCount(pageCount);

        //把传入的当前页码,再封装进pojo里,返回给调用者.
        searchResult.setCurPage(page);

        return searchResult;
    }
}
