import React from "react";
import SwaggerUIReact from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";
import { urls } from "@scm-manager/ui-components";
import styled from "styled-components";

const SwaggerUIContainer = styled.div`
  .version {
    background-color: inherit;
  }
  
  .main {
    min-height: inherit;
  }
`;

export default () => (
  <SwaggerUIContainer>
    <SwaggerUIReact url={urls.withContextPath("/api/v2/openapi")} />
  </SwaggerUIContainer>
);
