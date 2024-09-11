/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

import React, { FC } from "react";
import { Link } from "react-router-dom";
import { Me, Links } from "@scm-manager/ui-types";
import { useTranslation } from "react-i18next";

type Props = {
  me: Me;
  links: Links;
};

const RestDocumentationLink: FC<Props> = ({ links, me }) => {
  const [t] = useTranslation("plugins");
  if (links?.openapi && me) {
    return (
      <li>
        <Link to={"/openapi"}>{t("scm-openapi-plugin.restDocumentation")}</Link>
      </li>
    );
  }
  return null;
};

export default RestDocumentationLink;
