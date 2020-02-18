import { binder } from "@scm-manager/ui-extensions";
import { Loading } from "@scm-manager/ui-components";
import React, { Suspense } from "react";
import { Route } from "react-router-dom";
import RestDocumentationLink from "./RestDocumentationLink";

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

binder.bind("main.route", SwaggerUIRoute);

binder.bind("footer.links", RestDocumentationLink);
