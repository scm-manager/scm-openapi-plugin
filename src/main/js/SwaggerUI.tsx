import React, { FC, useEffect, useState } from "react";
import SwaggerUIReact from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";
import { apiClient, ErrorPage, Loading } from "@scm-manager/ui-components";
import styled from "styled-components";
import { useTranslation } from "react-i18next";

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
