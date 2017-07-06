#!/bin/bash
#
# Generate javadoc to be published at GitHub project page.
# 


version=1.0
project_root_dir=$(cd $(dirname $0)/..; pwd)
src_dir=${project_root_dir}/src/main/java
out_dir=${project_root_dir}/docs/javadoc/${version}

rm -rf ${out_dir}
mkdir ${out_dir}

javadoc -d ${out_dir}  \
   -sourcepath ${src_dir} \
   -windowtitle "Variant Experiment Server" \
   -doctitle "Variant Java Servlet Adapter ${version}" \
   -header "<a onclick=\"window.top.location.href='http://getvariant.com';\" href=\"#\"> <img style=\"margin-bottom:5px;\" src=\"http://getvariant.com/wp-content/uploads/2016/05/VariantLogoSmall.png\"/> \</a>" \
   -bottom "Version $version :: Copyright &copy; 2017 Variant :: Generated on $(date +"%d %b %Y")." \
   com.variant.client.servlet         \
   com.variant.client.servlet.impl    \
   com.variant.client.servlet.util    \

