import React, { FC, useEffect, useState } from "react";
import SwaggerUIReact from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";
import { apiClient, Loading, ErrorPage } from "@scm-manager/ui-components";
import styled from "styled-components";

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
  useEffect(() => {
    apiClient
      .get("openapi")
      .then(response => response.json())
      .then(setSpec)
      .catch(setError);
  }, []);

  if (error) {
    // TODO i18n
    return <ErrorPage title="OpenAPI" subtitle="Failed to load OpenAPI spec" error={error} />;
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
