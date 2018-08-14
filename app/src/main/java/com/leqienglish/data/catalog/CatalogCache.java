package com.leqienglish.data.catalog;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.database.ExecuteSQL;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.entity.english.Catalog;

import static com.leqienglish.database.Constants.CATALOG_TYPE;

public class CatalogCache extends DataCacheAbstract<List<Catalog>> {

    private static CatalogCache catalogCache;

    private CatalogCache() {

    }


    public static CatalogCache getInstance() {
        if (catalogCache != null) {
            return catalogCache;
        }
        synchronized (UserDataCache.class) {
            if (catalogCache == null) {
                catalogCache = new CatalogCache();
            }
        }

        return catalogCache;
    }
    @Override
    protected List<Catalog> getFromCache() {
       List<Catalog> catalogs =  ExecuteSQL.getDatasByType(CATALOG_TYPE,Catalog.class);
       if(catalogs == null || catalogs.isEmpty()){
           return null;
       }
        return catalogs;
    }

    @Override
    protected void putCache(List<Catalog> catalogs) {
        ExecuteSQL.insertLearnE(catalogs,null,CATALOG_TYPE);
    }

    @Override
    protected List<Catalog> getFromService() {

        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("type", Consistent.CATALOG_TYPE+"");
        try {
            Catalog[] contents = this.getRestClient().get("/english/catalog/getAllCatalogsByType",param,Catalog[].class);

            return Arrays.asList(contents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(List<Catalog> catalogs) {

    }

    @Override
    public void remove(List<Catalog> catalogs) {

    }
}
