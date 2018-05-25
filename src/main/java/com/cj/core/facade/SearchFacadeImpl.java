package com.cj.core.facade;

import com.alibaba.dubbo.config.annotation.Service;
import com.cj.core.facade.search.SearchFacade;
import com.cj.core.pojo.SearchResult;
import com.cj.core.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 搜索服务的service
 * 2018/5/17
 * @author cj
 */
@Service(version = "1.0.0")
public class SearchFacadeImpl implements SearchFacade{

    @Autowired
    private SearchService searchService;

    @Override
    public SearchResult search(String queryString, int page, int rows) throws Exception  {
        return searchService.search(queryString,page,rows);
    }
}
