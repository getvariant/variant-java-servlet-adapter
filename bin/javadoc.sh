#!/bin/bash
#
# Generate javadoc to be published at GitHub project page.
# 


version=0.10.3
root_dir=$(cd $(dirname $0)/..; pwd)
javadoc_dir=${root_dir}/docs

rm -rf ${javadoc_dir}/*

javadoc -Xdoclint:none --allow-script-in-comments -d ${javadoc_dir}  \
   -classpath ${root_dir}/lib/variant-java-client-${version}.jar:${root_dir}/lib/variant-core-${version}.jar:\
   -sourcepath ${root_dir}/src/main/java \
   -windowtitle "Variant ${version}" \
   -doctitle "Servlet Adapter for Variant Java Client" \
   -header "<a onclick=\"window.top.location.href='http://getvariant.com';\" href=\"#\"> <img style=\"margin-bottom:5px;\" src=\"http://getvariant.com/wp-content/uploads/2016/05/VariantLogoSmall.png\"/> \</a>" \
   -bottom "Release $version. Updated $(date +"%d %b %Y").<br/> Copyright &copy; 2019 <a onclick=\"window.top.location.href='http://getvariant.com';\" href=\"#\">Variant Inc.</a>" \
   com.variant.client.servlet         \
   com.variant.client.servlet.util 
