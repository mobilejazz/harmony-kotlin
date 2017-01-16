package com.worldreader.core.application.helper.analytics;

public class ProductList {

  private String productList;

  private ProductList(String productList) {
    this.productList = productList;
  }

  public void addMoreBy(String value) {
    this.productList = this.productList + " - More by " + value;
  }

  public void addMoreShelveInfo(String value) {
    this.productList = this.productList + " - " + value;
  }

  public String getProductList() {
    return this.productList;
  }

  //public static ProductList create(FromScreen fromScreen, ShelveModel shelveModel) {
  //  ProductList productList;
  //  if (fromScreen == FromScreen.CATEGORY_SCREEN) {
  //    productList = new ProductList(fromScreen.getValue());
  //    productList.addMoreBy(shelveModel.getTitle());
  //  } else {
  //    productList = new ProductList(shelveModel.getTitle());
  //  }
  //
  //  return productList;
  //}

  public static ProductList create(String shelveTitle) {
    return new ProductList(shelveTitle);
  }

  public static ProductList create(String screenTitle, String shelveTitle) {
    ProductList productList = new ProductList(screenTitle);
    productList.addMoreBy(shelveTitle);

    return productList;
  }
}
