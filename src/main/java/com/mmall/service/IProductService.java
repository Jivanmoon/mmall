package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by Jivan on 2020/1/27.
 */
public interface IProductService {

    public ServerResponse saveOrUpdateProduct(Product product);

    public ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    public ServerResponse getProductList(int pageNum, int pageSize);

    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    public ServerResponse<PageInfo> getProductByKeyWordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);

}
