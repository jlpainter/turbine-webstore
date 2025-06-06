/*
 *  ------------------------------------------------------------------
 *  JiveCast
 *  301 Fayetteville St Unit 2301
 *  Raleigh, NC 27601
 *  https://jivecast.com
 *
 *  Copyright (c) 2018-2025 JiveCast. All Rights Reserved.
 *
 *  THE PROGRAM IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EITHER EXPRESSED OR IMPLIED, INCLUDING, WITHOUT
 *  LIMITATION, WARRANTIES THAT THE PROGRAM IS FREE OF
 *  DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR
 *  NON-INFRINGING. THE ENTIRE RISK AS TO THE QUALITY AND
 *  PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD ANY PART
 *  OF THE PROGRAM PROVE DEFECTIVE IN ANY RESPECT, YOU
 *  (NOT JIVECAST) ASSUME THE COST OF ANY NECESSARY SERVICING,
 *  REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES
 *  AN ESSENTIAL PART OF THIS LICENSE. NO USE OF
 *  THE PROGRAM IS AUTHORIZED HEREUNDER EXCEPT
 *  UNDER THIS DISCLAIMER.
 *
 *  ------------------------------------------------------------------
 */

package com.jivecast.webstore.services.pull;



import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.json.JsonService;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.turbine.TurbineAccessControlList;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.pull.RunDataApplicationTool;
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.security.SecurityService;


public class RelatedTool implements RunDataApplicationTool {
  
  /** Logging */
  private static Log log = LogFactory.getLog(RelatedTool.class);
    
  private RunData data = null;

  @Override
  public void init(Object data) {
    this.data = (RunData) data;

  }
  
  @Override
  public void refresh(RunData data) {
    this.data = data;
  }
  
  public String getUserrole() {
    try {
      User user = data.getUser();
      Role role = null;
      if (user != null && user.getName() != null) {
          log.info("reading role for: "+user.getName());
          if (data.getACL() != null && data.getACL() instanceof TurbineAccessControlList) {
            RoleSet roles = ((TurbineAccessControlList)data.getACL()).getRoles();
            if (roles != null && roles.getSet().size() == 1) {
              Role fulcrumRole = roles.getSet().iterator().next();
                log.debug("acl role is: "+fulcrumRole.getName());
                return fulcrumRole.getName();
            }
          } else {
            SecurityService securityService = (SecurityService) TurbineServices.getInstance().getService(SecurityService.SERVICE_NAME);
            RoleSet roles = securityService.getAllRoles();   				
          }
      } else if (user != null) {
        SecurityService securityService = (SecurityService) TurbineServices.getInstance().getService(SecurityService.SERVICE_NAME);
          role = securityService.getRoleByName("user");
              //.retrieveRole("anon");				
      }
      return (role != null)? role.getName(): null;
    } catch (Exception e) {
      log.error("RelatedTool - failure in reading role: ", e);
      return null;
    }
  }
  
  @SuppressWarnings( "unchecked" )
    public <T> Object getJson(Object src, String className, String mixinCN, Boolean refresh, String... props ) {
        String result= null;
        JsonService jsonService = (JsonService)TurbineServices
        .getInstance().getService(JsonService.ROLE);
        try
        {
            log.info( "refresh is:"+ refresh );
            log.info( "jsonService:"+ jsonService );
            log.info( "source class is:"+ className );
            log.info( "target object is:"+ src );
            Class clazz = Class.forName(className);
            if (props != null) {
                log.info( "props length:"+ props.length);
                  for ( int i = 0; i < props.length; i++ )
                {
                      log.debug( "props:"+ props[i]);
                }
            }
            if (mixinCN != null ) {
               Class mixin = Class.forName(mixinCN);
               if (mixin != null ) {
                 Set<Class> mixins = new HashSet<Class>();
                 mixins.add( mixin );
                   log.info( "adding adapter mixinCN:"+ mixinCN);
                   jsonService.addAdapter( mixinCN, clazz, mixin );
               }
            }
            String serialized =  jsonService.serializeOnlyFilter( src, clazz, refresh, props );
            log.debug( "serialized:"+serialized );
            return serialized;
        }
        catch ( Exception e )
        {
            log.error(e.getMessage(),e );
            result = e.getMessage();
        }
        return result;
    }

}
