import React, { Suspense } from "react";
import { Route } from "react-router-dom";
import { Loading } from "@scm-manager/ui-components";

const SwaggerUIRoute = () => {
  const SuspendingSwaggerUI = () => {
    const LoadingSwaggerUI = React.lazy(() => import("./SwaggerUI"));
    return (
      <Suspense fallback={<Loading />}>
        <LoadingSwaggerUI />
      </Suspense>
    );
  };
  return <Route path="/openapi" component={SuspendingSwaggerUI} />;
};

export default SwaggerUIRoute;
