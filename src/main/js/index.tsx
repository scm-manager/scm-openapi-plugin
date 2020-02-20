import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import SwaggerUIRoute from "./SwaggerUIRoute";
import RestDocumentationLink from "./RestDocumentationLink";

binder.bind("main.route", SwaggerUIRoute);
binder.bind("footer.information", RestDocumentationLink);
