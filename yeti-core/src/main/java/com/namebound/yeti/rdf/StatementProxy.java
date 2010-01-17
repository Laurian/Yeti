package com.namebound.yeti.rdf;

import com.namebound.yeti.resource.Resource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;

/**
 *
 * @author Laurian Gridinoc
 */
public class StatementProxy implements InvocationHandler {

    private Statement statement;

    public static Statement newInstance(Statement statement) {
        return (Statement) java.lang.reflect.Proxy.newProxyInstance(
                statement.getClass().getClassLoader(),
                statement.getClass().getInterfaces(),
                new StatementProxy(statement));
    }

    private StatementProxy(Statement statement) {
        this.statement = statement;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Object result;
        try {
            result = method.invoke(statement, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        }

        if (result instanceof URI) {
            result = new Resource((URI) result);
        }

        return result;
    }
}
