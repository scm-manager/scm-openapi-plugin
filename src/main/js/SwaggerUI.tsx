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

import React, { FC, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import SwaggerUIReact from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";
import styled from "styled-components";
import { apiClient, ErrorPage, Loading } from "@scm-manager/ui-components";
import { useDocumentTitle } from "@scm-manager/ui-core";
import "./swaggerUIColors.css";

const SwaggerUIContainer = styled.div`
  .version {
    background-color: inherit;
  }

  .main {
    min-height: inherit;
  }
`;

const SwaggerUI: FC = () => {
  const [spec, setSpec] = useState(undefined);
  const [error, setError] = useState<Error | undefined>(undefined);
  const [t] = useTranslation("plugins");
  useDocumentTitle(t("scm-openapi-plugin.navLink"));

  useEffect(() => {
    apiClient
      .get("openapi")
      .then(response => response.json())
      .then(setSpec)
      .catch(setError);
  }, []);

  if (error) {
    return (
      <ErrorPage
        title={t("scm-openapi-plugin.error.title")}
        subtitle={t("scm-openapi-plugin.error.subtitle")}
        error={error}
      />
    );
  }

  if (!spec) {
    return <Loading />;
  }

  return (
    <SwaggerUIContainer>
      <SwaggerUIReact spec={spec} />
    </SwaggerUIContainer>
  );
};

export default SwaggerUI;
