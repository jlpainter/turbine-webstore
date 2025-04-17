package com.jivecast.webstore.wrapper;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/*
 * Copyright 2001-2025 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.fulcrum.security.model.turbine.entity.TurbineUser;
import org.apache.turbine.om.security.DefaultUserImpl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jivecast.webstore.om.CompanyConfig;
import com.jivecast.webstore.om.HomePage;
import com.jivecast.webstore.om.ItemCategory;

/**
 * Custom Wrapper instead fo default Turbine wrapper class
 * {@link DefaultUserImpl} to allow for annotations, - th single requirment for
 * wrappers is to implement a constructor with parameter {@link TurbineUser}
 *
 */
@JsonIgnoreProperties({ "permStorage", "type", "objectdata" })
public class TurbineUserWrapper extends DefaultUserImpl {
	public TurbineUserWrapper(TurbineUser user) {
		super(user);
	}

	public String getHomepageMessage() {
		String name = "";
		try {
			HomePage conf = HomePage.getDefault();
			name = conf.getWelcomeMessage().trim();
		} catch (Exception e) {

		}
		return name;
	}

	public String getHomepageImage() {
		try {
			HomePage conf = HomePage.getDefault();
			String name = conf.getFileName();
			if (!StringUtils.isEmpty(name))
				return name;

		} catch (Exception e) {
		}
		return null;
	}

	public String getCompanyName() {
		String name = "";
		try {
			CompanyConfig conf = CompanyConfig.getDefault();
			name = conf.getCompanyName().trim();
		} catch (Exception e) {

		}
		return name;
	}

	public String getAdminEmail() {
		String name = "";
		try {
			CompanyConfig conf = CompanyConfig.getDefault();
			name = conf.getAdminEmail();
		} catch (Exception e) {

		}
		return name;

	}

	public List<ItemCategory> getItemCategories() {
		return ItemCategory.getAllCategories();
	}
}
