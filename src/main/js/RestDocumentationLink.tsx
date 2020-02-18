import React, { FC } from "react";
import { Link } from "react-router-dom";
import { Me, Links } from "@scm-manager/ui-types";
import { useTranslation } from "react-i18next";

type Props = {
  me: Me;
  links: Links;
};

const RestDocumentationLink: FC<Props> = ({ links }) => {
  const [t] = useTranslation("plugins");
  if (links?.openapi) {
    return <Link to={"/openapi"}>{t("scm-openapi-plugin.restDocumentation")}</Link>;
  }
  return null;
};

export default RestDocumentationLink;
