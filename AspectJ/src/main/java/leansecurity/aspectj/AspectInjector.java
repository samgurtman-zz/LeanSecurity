package leansecurity.aspectj;

import leansecurity.aspects.SecurityEvaluator;
import org.aspectj.lang.Aspects;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by sam on 13/05/16.
 */
@Named
public class AspectInjector {

    @Inject
    public AspectInjector(SecurityEvaluator securityEvaluator){
        Aspects.aspectOf(Pointcuts.class).setSecurityEvaluator(securityEvaluator);
    }
}
