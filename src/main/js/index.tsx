import { binder } from "@scm-manager/ui-extensions";
import React from "react";
import { Route } from "react-router-dom";
import SwaggerUI from "./SwaggerUI";

const SwaggerUIRoute = () => {
  return <Route path="/openapi" render={() => <SwaggerUI />} />;
};

binder.bind("main.route", SwaggerUIRoute);
