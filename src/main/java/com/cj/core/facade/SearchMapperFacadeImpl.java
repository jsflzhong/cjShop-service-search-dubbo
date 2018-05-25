package com.cj.core.facade;

import com.alibaba.dubbo.config.annotation.Service;
import com.cj.core.facade.search.SearchMapperFacade;
import com.cj.core.pojo.SearchResult;
import com.cj.core.service.SearchMapper;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Search服务的dao层
 * 从Solr服务器中,查询搜索结果的mapper.
 * @author 崔健
 * @date 2016年12月16日上午12:20:40
 */
@Service(version = "1.0.0")
public class SearchMapperFacadeImpl implements SearchMapperFacade{

	@Autowired
	private SearchMapper searchMapper;

	public SearchResult search(SolrQuery query) throws Exception {
		return searchMapper.search(query);
	}
}
