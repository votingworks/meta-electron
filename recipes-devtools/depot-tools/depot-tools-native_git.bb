# File taken and modified from https://github.com/jwinarske/meta-flutter
#
# This recipe provides chromium's build toolchain, depot_tools. These tools are
# very similar to Yocto's own, which creates lots of problems. 
#
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c2c05f9bdd5fc0b458037c2d1fb8d95e"

SRC_URI = "git://chromium.googlesource.com/chromium/tools/depot_tools;protocol=https \
           file://ca-certificates.crt;name=certs"

SRCREV = "3a56ba9d9c9d22bc78e24f96a9096247d53649f8"

SRC_URI[certs.md5sum] = "1ecab07e89925a6e8684b75b8cf84890"

S = "${WORKDIR}/git"

#RDEPENDS_${PN}-dev = "\
#    python_2.7.18 \
#    perl \
#    gcc \
#    glibc-dev \
#"

do_compile() {

    # force bootstrap download to get python2
    
    cd ${S}
    export DEPOT_TOOLS_UPDATE=0
    export GCLIENT_PY3=1
    export PATH=${S}:$PATH

    gclient --version
}

do_install() {

    install -d ${D}${datadir_native}/depot_tools
#    cp -rTv ${S}/. ${D}${datadir_native}/depot_tools/
    #cp -r ${S} ${D}${datadir_native}/depot_tools/

    # We don't need this
    #rm ${D}#{datadir_native}/depot_tools/git/ninja-linux32

    cp ${S}/gclient ${D}${datadir_native}/depot_tools/
    cp ${S}/gclient.py ${D}${datadir_native}/depot_tools/

    cp ${S}/cipd ${D}${datadir_native}/depot_tools/

    cp ${S}/vpython3 ${D}${datadir_native}/depot_tools/

    cp ${S}/cipd_bin_setup.sh ${D}${datadir_native}/depot_tools/
    cp ${S}/cipd_manifest.txt ${D}${datadir_native}/depot_tools/

    install -d ${D}${datadir_native}/depot_tools/.cipd_bin
    cp ${S}/.cipd_bin/vpython3 ${D}${datadir_native}/depot_tools/.cipd_bin/


    cp ${S}/python3_bin_reldir.txt ${D}${datadir_native}/depot_tools/

    install -m 644 ${WORKDIR}/ca-certificates.crt ${D}${datadir_native}/depot_tools
}

#FILES_${PN} = "${datadir_native}/depot_tools/*"
FILES_${PN}-dev = "\
    ${datadir_native}/depot_tools/* \
    ${datadir_native}/depot_tools/.cipd_bin/* \
"
#    ${datadir_native}/depot_tools/.cipd_bin/.vpython \
#    ${datadir_native}/depot_tools/.cipd_bin/.versions/* \
#    ${datadir_native}/depot_tools/.cipd_bin/.cipd/* \
#    ${datadir_native}/depot_tools/.cipd_client_cache/* \
#    ${datadir_native}/depot_tools/git-templates/* \
#    ${datadir_native}/depot_tools/python2-bin/* \
#    ${datadir_native}/depot_tools/bootstrap/* \
#    ${datadir_native}/depot_tools/.versions \
#    ${datadir_native}/depot_tools/.vpython3 \
#    ${datadir_native}/depot_tools/.vpython \
#    ${datadir_native}/depot_tools/.cipd_client \
#    ${datadir_native}/depot_tools/.cipd_impl.ps1 \
#    ${datadir_native}/depot_tools/.git/* \
#    ${datadir_native}/depot_tools/.gitattributes \
#    ${datadir_native}/depot_tools/.gitignore\
#    ${datadir_native}/depot_tools/.style.yapf \
#"


INSANE_SKIP_${PN} = "already-stripped"
INSANE_SKIP_${PN}-dev = "already-stripped"

BBCLASSEXTEND += "native"
