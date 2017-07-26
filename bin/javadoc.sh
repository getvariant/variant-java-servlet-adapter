#!/bin/bash
#
# Generate javadoc to be published at GitHub project page.
# 


version=1.0
root_dir=$(cd $(dirname $0)/..; pwd)
javadoc_dir=${root_dir}/docs

rm -rf ${javadoc_dir}/*

javadoc -d ${javadoc_dir}  \
   -sourcepath ${root_dir}/servlet-adapter/src/main/java \
   -windowtitle "Variant ${version}" \
   -doctitle "Servlet Adapter for Variant Java Client" \
   -header "<a onclick=\"window.top.location.href='http://getvariant.com';\" href=\"#\"> <img style=\"margin-bottom:5px;\" src=\"http://getvariant.com/wp-content/uploads/2016/05/VariantLogoSmall.png\"/> \</a>" \
   -bottom "Release $version. Updated $(date +"%d %b %Y").<br/> Copyright &copy; 2017 <a onclick=\"window.top.location.href='http://getvariant.com';\" href=\"#\">Variant Inc.</a>" \
   com.variant.client.servlet         \
   com.variant.client.servlet.impl    \
   com.variant.client.servlet.util    \

