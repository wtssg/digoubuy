package wtssg.xdly.digoubuytradeservice.product.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import wtssg.xdly.digoubuytradeservice.common.constants.Constants;
import wtssg.xdly.digoubuytradeservice.product.dao.CategoryMapper;
import wtssg.xdly.digoubuytradeservice.product.dao.ProductMapper;
import wtssg.xdly.digoubuytradeservice.product.entity.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("productServiceImpl")
public class ProductServiceImpl implements ProductService {

    private static final int DEFAULT_PAGE_SIZE = 15;
    private static final int DEFAULT_PAGE_NUMBER = 0;

    private static final int SORT_OVERALL = 0;
    private static final int SORT_REGISTER_DATE = 1;
    private static final int SORT_PRICE_UP = 2;
    private static final int SORT_PRICE_DOWN = 3;

    @Autowired
    private JestHttpClient esClient;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 按照关键字搜索
     * @param pageNumber
     * @param pageSize
     * @param searchContent
     * @return
     * @throws IOException
     */
    @Override
    public EsSearchResult searchProduct(int pageNumber, int pageSize, int searchCondition, String searchContent) throws IOException {
        if (pageSize == 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders
                .boolQuery()
                .must(QueryBuilders
                        .matchQuery("title_name", searchContent))
                .must(QueryBuilders
                        .matchQuery("status", 1)))
                .from(pageNumber)
                .size(pageSize);
        if (searchCondition == SORT_OVERALL) {
            searchSourceBuilder.sort("title_price" , SortOrder.DESC);
        } else if (searchCondition == SORT_REGISTER_DATE) {
            searchSourceBuilder.sort("create_time" , SortOrder.DESC);
        } else if (searchCondition == SORT_PRICE_UP) {
            searchSourceBuilder.sort("title_price" , SortOrder.DESC);
        } else if (searchCondition == SORT_PRICE_DOWN) {
            searchSourceBuilder.sort("title_price" , SortOrder.ASC);
        }
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("digou-buy-trade")
                .build();
        SearchResult result = esClient.execute(search);
        return parseResult(result.getJsonString());
    }

    /**
     * 按照类别查找
     * @param categoryId
     * @return
     * @throws IOException
     */
    @Override
    public EsSearchResult searchByCategory(int categoryId) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders
                .boolQuery()
                .must(QueryBuilders
                        .matchQuery("category_id", categoryId))
                .must(QueryBuilders
                        .matchQuery("status", 1)));
        searchSourceBuilder.sort("title_price" , SortOrder.DESC);
        String s = searchSourceBuilder.toString();
        System.out.println(s);
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("digou-buy-trade")
                .build();
        SearchResult result = esClient.execute(search);
        return parseResult(result.getJsonString());
    }

    /**
     * 查询所有的类别数据返回给前端，并且缓存到redis
     * @return
     */
    @Override
//    @Cacheable(cacheNames = Constants.CACHE_PRODUCT_CATEGORY)
    public List<Category> listCategory() {
        return categoryMapper.selectAll();
    }

    /**
     * 根据商品id获得商品的所有sku和参数选项列表
     * @param productId
     * @return
     */
    @Override
    @Cacheable(cacheNames = Constants.CACHE_PRODUCT_DETAIL, key = "#productId")
    public PropertyOptionAndProductDetail productDetail(int productId) {
        Product product =productMapper.selectProductDetail(productId);
        List<ProductSku> skuList = product.getSkuList();
        List<SkuPropertyOption> skuPropertyOptionList = skuList.get(0).getSkuPropertyOptionList();;
        List<SkuPropertyDetail> list = new ArrayList<>();
        for (int i = 0; i < skuPropertyOptionList.size(); i++) {
            SkuPropertyDetail skuPropertyDetail = new SkuPropertyDetail();
            skuPropertyDetail.setPropertyName(skuPropertyOptionList.get(i).getPropertyName());
            skuPropertyDetail.setOptionList(new ArrayList<>());
            list.add(skuPropertyDetail);
        }
        SkuPropertyDetail skuPropertyDetail = new SkuPropertyDetail();
        for (int i = 0; i < skuList.size(); i++) {
            skuPropertyOptionList = skuList.get(i).getSkuPropertyOptionList();
            for (int j = 0; j < skuPropertyOptionList.size(); j++) {
                List<String> optionList = list.get(j).getOptionList();
                String optionName = skuPropertyOptionList.get(j).getOptionName();
                if (!optionList.contains(optionName)) {
                    optionList.add(optionName);
                }
            }
        }
        PropertyOptionAndProductDetail result = new PropertyOptionAndProductDetail();
        result.setProduct(product);
        result.setPropertyOptionList(list);
        return result;
    }


    /**
     * json转product对象
     * @param jsonString
     * @return
     */
    private EsSearchResult parseResult(String jsonString) {
        EsSearchResult esSearchResult = new EsSearchResult();
        JSONObject jsonOb = JSON.parseObject(jsonString).getJSONObject("hits");
        esSearchResult.setTotal(jsonOb.getIntValue("total"));
        JSONArray jsonArray = jsonOb.getJSONArray("hits");
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject productObj = jsonObject.getJSONObject("_source");
            Product product = new Product();
            product.setId(productObj.getLongValue("id"));
            product.setCategoryId(productObj.getLongValue("category_id"));
            product.setBrandId(productObj.getLongValue("brand_id"));
            product.setSpuName(productObj.getString("spu_name"));
            product.setTitleName(productObj.getString("title_name"));
            product.setTitleImgUrl(productObj.getString("title_img_url"));
            product.setTitlePrice(productObj.getBigDecimal("title_price"));
            productList.add(product);
        }
        esSearchResult.setProductList(productList);
        return esSearchResult;
    }

}
