package com.xiaoxinyun.answerservice.repository.tkmapper;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MyBatis执行sql工具
 *
 * @author liuzh
 * @since 2015-03-10
 */
@Component
public class SqlMapper {
    //  private  MSUtils msUtils;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;


    /**
     * 构造方法，默认缓存MappedStatement
     *
     * @param
     */
    public SqlMapper() {
    }

    /**
     * 获取List中最多只有一个的数据
     *
     * @param list List结果
     * @param <T>  泛型类型
     * @return
     */
    private <T> T getOne(List<T> list) {
        if (list.size() > 0) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    /**
     * 查询返回一个结果，多个结果时抛出异常
     *
     * @param sql 执行的sql
     * @return
     */
    public Map<String, Object> selectOne(String sql) {
        List<Map<String, Object>> list = selectList(sql);
        return getOne(list);
    }

    /**
     * 查询返回一个结果，多个结果时抛出异常
     *
     * @param sql   执行的sql
     * @param value 参数
     * @return
     */
    public Map<String, Object> selectOne(String sql, Object value) {
        List<Map<String, Object>> list = selectList(sql, value);
        return getOne(list);
    }

    /**
     * 查询返回一个结果，多个结果时抛出异常
     *
     * @param sql        执行的sql
     * @param resultType 返回的结果类型
     * @param <T>        泛型类型
     * @return
     */
    public <T> T selectOne(String sql, Class<T> resultType) {
        List<T> list = selectList(sql, resultType);
        return getOne(list);
    }

    /**
     * 查询返回一个结果，多个结果时抛出异常
     *
     * @param sql        执行的sql
     * @param value      参数
     * @param resultType 返回的结果类型
     * @param <T>        泛型类型
     * @return
     */
    public <T> T selectOne(String sql, Object value, Class<T> resultType) {
        List<T> list = selectList(sql, value, resultType);
        return getOne(list);
    }

    /**
     * 查询返回List<Map<String, Object>>
     *
     * @param sql 执行的sql
     * @return
     */
    public List<Map<String, Object>> selectList(String sql) {
        List<Map<String, Object>> result = null;
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {

            MSUtils msUtils = new MSUtils(sqlSession.getConfiguration());
            String msId = msUtils.select(sql);
            result = sqlSession.selectList(msId);
        } catch (Exception ex) {
            throw ex;
        } finally {
            sqlSession.close();
        }

        return result;
    }

    /**
     * 查询返回List<Map<String, Object>>
     *
     * @param sql   执行的sql
     * @param value 参数
     * @return
     */
    public List<Map<String, Object>> selectList(String sql, Object value) {
        List<Map<String, Object>> result;
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            MSUtils msUtils = new MSUtils(sqlSession.getConfiguration());

            Class<?> parameterType = value != null ? value.getClass() : null;
            String msId = msUtils.selectDynamic(sql, parameterType);
            result = sqlSession.selectList(msId, value);
        } catch (Exception ex) {
            throw ex;
        } finally {
            sqlSession.close();
        }
        return result;
    }

    /**
     * 查询返回指定的结果类型
     *
     * @param sql        执行的sql
     * @param resultType 返回的结果类型
     * @param <T>        泛型类型
     * @return
     */
    public <T> List<T> selectList(String sql, Class<T> resultType) {
        List<T> result = null;
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            MSUtils msUtils = new MSUtils(sqlSession.getConfiguration());

            String msId;
            if (resultType == null) {
                msId = msUtils.select(sql);
            } else {
                msId = msUtils.select(sql, resultType);
            }
            result = sqlSession.selectList(msId);
        } catch (Exception ex) {
            throw ex;
        } finally {
            sqlSession.close();
        }
        return result;
    }

    /**
     * 查询返回指定的结果类型
     *
     * @param sql        执行的sql
     * @param value      参数
     * @param resultType 返回的结果类型
     * @param <T>        泛型类型
     * @return
     */
    public <T> List<T> selectList(String sql, Object value, Class<T> resultType) {
        List<T> result = null;
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            MSUtils msUtils = new MSUtils(sqlSession.getConfiguration());

            String msId;
            Class<?> parameterType = value != null ? value.getClass() : null;
            if (resultType == null) {
                msId = msUtils.selectDynamic(sql, parameterType);
            } else {
                msId = msUtils.selectDynamic(sql, parameterType, resultType);
            }
            result = sqlSession.selectList(msId, value);
        } catch (Exception ex) {
            throw ex;
        } finally {
            sqlSession.close();
        }
        return result;
    }


    ///////////////////////分页 bagin/////////////////////

    /**
     * 分页查询 使用 PageHelper 进行分页
     *
     * @param sql
     * @param pageNo
     * @param pageSize
     * @param resultType
     * @param <T>
     * @return
     */
    public <T> PageInfo<T> selectListPage(String sql, Integer pageNo, Integer pageSize, Class<T> resultType) {
        return selectListPage(sql, null, pageNo, pageSize, resultType);
    }

    public <T> PageInfo<T> selectListPage(String sql, Object parms, Integer pageNo, Integer pageSize, Class<T> resultType) {
        pageNo = (pageNo == null || pageNo < 1) ? 1 : pageNo;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        //设置分页
        PageHelper.startPage(pageNo, pageSize);

        PageInfo<T> pageInfo;
        if (parms != null) {
            pageInfo = new PageInfo(selectList(sql, parms, resultType));
        } else {
            pageInfo = new PageInfo(selectList(sql, resultType));
        }
        return pageInfo;
    }



    ///////////////////////分页 end/////////////////////

    /**
     * 插入数据
     *
     * @param sql 执行的sql
     * @return
     */
    public int insert(String sql) {
        int result = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            MSUtils msUtils = new MSUtils(sqlSession.getConfiguration());
            String msId = msUtils.insert(sql);
            result = sqlSession.insert(msId);
        } catch (Exception ex) {
            throw ex;
        } finally {
            sqlSession.close();
        }
        return result;
    }


    /**
     * 插入数据
     *
     * @param sql   执行的sql
     * @param value 参数
     * @return
     */
    public int insert(String sql, Object value) {
        int result = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            MSUtils msUtils = new MSUtils(sqlSession.getConfiguration());

            Class<?> parameterType = value != null ? value.getClass() : null;
            String msId = msUtils.insertDynamic(sql, parameterType);
            result = sqlSession.insert(msId, value);

        } catch (Exception ex) {
            throw ex;
        } finally {
            sqlSession.close();
        }
        return result;
    }

    /**
     * 更新数据
     *
     * @param sql 执行的sql
     * @return
     */
    public int update(String sql) {
        int result = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            MSUtils msUtils = new MSUtils(sqlSession.getConfiguration());
            String msId = msUtils.update(sql);
            result = sqlSession.update(msId);
        } catch (Exception ex) {
            throw ex;
        } finally {
            sqlSession.close();
        }
        return result;
    }

    /**
     * 更新数据
     *
     * @param sql   执行的sql
     * @param value 参数
     * @return
     */
    public int update(String sql, Object value) {
        int result = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            MSUtils msUtils = new MSUtils(sqlSession.getConfiguration());

            Class<?> parameterType = value != null ? value.getClass() : null;
            String msId = msUtils.updateDynamic(sql, parameterType);

            result = sqlSession.update(msId, value);
        } catch (Exception ex) {
            throw ex;
        } finally {
            sqlSession.close();
        }
        return result;


    }

    /**
     * 删除数据
     *
     * @param sql 执行的sql
     * @return
     */
    public int delete(String sql) {
        int result = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            MSUtils msUtils = new MSUtils(sqlSession.getConfiguration());

            String msId = msUtils.delete(sql);
            result = sqlSession.delete(msId);

        } catch (Exception ex) {
            throw ex;
        } finally {
            sqlSession.close();
        }
        return result;
    }

    /**
     * 删除数据
     *
     * @param sql   执行的sql
     * @param value 参数
     * @return
     */
    public int delete(String sql, Object value) {
        int result = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            MSUtils msUtils = new MSUtils(sqlSession.getConfiguration());

            Class<?> parameterType = value != null ? value.getClass() : null;
            String msId = msUtils.deleteDynamic(sql, parameterType);
            result = sqlSession.delete(msId, value);

        } catch (Exception ex) {
            throw ex;
        } finally {
            sqlSession.close();
        }
        return result;
    }

    private class MSUtils {
        private Configuration configuration;
        private LanguageDriver languageDriver;

        private MSUtils(Configuration configuration) {
            this.configuration = configuration;
            languageDriver = configuration.getDefaultScriptingLanuageInstance();
        }

        /**
         * 创建MSID
         *
         * @param sql 执行的sql
         * @param sql 执行的sqlCommandType
         * @return
         */
        private String newMsId(String sql, SqlCommandType sqlCommandType) {
            StringBuilder msIdBuilder = new StringBuilder(sqlCommandType.toString());
            msIdBuilder.append(".").append(sql.hashCode());
            return msIdBuilder.toString();
        }

        /**
         * 是否已经存在该ID
         *
         * @param msId
         * @return
         */
        private boolean hasMappedStatement(String msId) {
            return configuration.hasStatement(msId, false);
        }

        /**
         * 创建一个查询的MS
         *
         * @param msId
         * @param sqlSource  执行的sqlSource
         * @param resultType 返回的结果类型
         */
        private void newSelectMappedStatement(String msId, SqlSource sqlSource, final Class<?> resultType) {
            MappedStatement ms = new MappedStatement.Builder(configuration, msId, sqlSource, SqlCommandType.SELECT)
                    .resultMaps(new ArrayList<ResultMap>() {
                        {
                            add(new ResultMap.Builder(configuration, "defaultResultMap", resultType, new ArrayList<ResultMapping>(0)).build());
                        }
                    })
                    .build();
            //缓存
            configuration.addMappedStatement(ms);
        }

        /**
         * 创建一个简单的MS
         *
         * @param msId
         * @param sqlSource      执行的sqlSource
         * @param sqlCommandType 执行的sqlCommandType
         */
        private void newUpdateMappedStatement(String msId, SqlSource sqlSource, SqlCommandType sqlCommandType) {
            MappedStatement ms = new MappedStatement.Builder(configuration, msId, sqlSource, sqlCommandType)
                    .resultMaps(new ArrayList<ResultMap>() {
                        {
                            add(new ResultMap.Builder(configuration, "defaultResultMap", int.class, new ArrayList<ResultMapping>(0)).build());
                        }
                    })
                    .build();
            //缓存
            configuration.addMappedStatement(ms);
        }

        private String select(String sql) {
            String msId = newMsId(sql, SqlCommandType.SELECT);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
            newSelectMappedStatement(msId, sqlSource, Map.class);
            return msId;
        }

        private String selectDynamic(String sql, Class<?> parameterType) {
            String msId = newMsId(sql + parameterType, SqlCommandType.SELECT);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
            newSelectMappedStatement(msId, sqlSource, Map.class);
            return msId;
        }

        private String select(String sql, Class<?> resultType) {
            String msId = newMsId(resultType + sql, SqlCommandType.SELECT);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
            newSelectMappedStatement(msId, sqlSource, resultType);
            return msId;
        }

        private String selectDynamic(String sql, Class<?> parameterType, Class<?> resultType) {
            String msId = newMsId(resultType + sql + parameterType, SqlCommandType.SELECT);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
            newSelectMappedStatement(msId, sqlSource, resultType);
            return msId;
        }

        private String insert(String sql) {
            String msId = newMsId(sql, SqlCommandType.INSERT);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
            newUpdateMappedStatement(msId, sqlSource, SqlCommandType.INSERT);
            return msId;
        }

        private String insertDynamic(String sql, Class<?> parameterType) {
            String msId = newMsId(sql + parameterType, SqlCommandType.INSERT);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
            newUpdateMappedStatement(msId, sqlSource, SqlCommandType.INSERT);
            return msId;
        }

        private String update(String sql) {
            String msId = newMsId(sql, SqlCommandType.UPDATE);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
            newUpdateMappedStatement(msId, sqlSource, SqlCommandType.UPDATE);
            return msId;
        }

        private String updateDynamic(String sql, Class<?> parameterType) {
            String msId = newMsId(sql + parameterType, SqlCommandType.UPDATE);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
            newUpdateMappedStatement(msId, sqlSource, SqlCommandType.UPDATE);
            return msId;
        }

        private String delete(String sql) {
            String msId = newMsId(sql, SqlCommandType.DELETE);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
            newUpdateMappedStatement(msId, sqlSource, SqlCommandType.DELETE);
            return msId;
        }

        private String deleteDynamic(String sql, Class<?> parameterType) {
            String msId = newMsId(sql + parameterType, SqlCommandType.DELETE);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
            newUpdateMappedStatement(msId, sqlSource, SqlCommandType.DELETE);
            return msId;
        }
    }
}

