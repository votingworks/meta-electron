# Build recipe for electron v7.2.4  
SUMMARY = "Build cross platform desktop apps with web technologies"
DESCRIPTION = "The Electron framework lets you write cross-platform \
desktop applications using JavaScript, HTML and CSS. It is based on \
io.js and Chromium and is used in the Atom editor."
HOMEPAGE = "http://electionjs.com/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=f8436f54558748146ec7ebd61ca6ac38 \
"


DEPENDS += " \
  ninja-native \
  nodejs-native \
  clang-cross-${TARGET_ARCH} \
  util-linux \
  libnotify3 \
  gtk+ \
  gconf \
  dbus \
  alsa-lib \
  cups \
  xinput \
  nss \
  libxtst \
  libxi \
  libcap \
  gn-native \
  depot-tools-native \
"

PV = "7.2.4"


SRC_URI = " \
  git://github.com/electron/electron.git;tag=v${PV};nobranch=1 \
  file://fix_chromium_build_config.patch;apply=no; \
"

S = "${WORKDIR}/git"

inherit electron-arch npm

# TODO: the following doesn't appear to cache the way we need :(
# Cache fetched chromium code
#SSTATETASKS += "do_sync"
#do_sync[sstate-plaindirs] = "${S}"
# 
#python do_sync_setscene() {
#    sstate_setscene(d)
#}
#addtask do_sync_setscene

DEPOT_TOOLS ?= "${datadir_native}/depot_tools"

do_sync_prepend() {
    # TODO: do we need python2?
    export PATH=${DEPOT_TOOLS}:$PATH
}

# Pull down the code using gclient, so that we get the correct version of the
# chromium dependency. We might be able to use meta-chromium, but it doesn't
# seem to have a mechanism for versioning that would let us get the version we
# need. 
#
# This task is slower than molasses in January.
#
# Derived from these instructions:
# https://github.com/electron/electron/blob/v7.2.4/docs/development/build-instructions-gn.md
do_sync() {
    cd ${S}

    gclient config --name "src/electron" --unmanaged https://github.com/electron/electron

    # Use git hash from 7.2.4 commit
    # Ideally this would be in SRCREV but because Electron doens't
    # do branching well I can't get that to work. 
    #
    # The revision peg is needed to avoid some issues with the 
    # PLV8 engine: https://github.com/plv8/plv8/issues/448
    #
    # What I *think* this is doing: using the githun repo to populate
    # and using gclient to sync the version of chromium required by electron.
    #
    # What I'm worried it *might* be doing: redownloading the whole electron
    # code base into a subdir inside the existing electron git repo. 
    gclient sync --revision 0552e0d5de46ffa3b481d741f1db5c779e201565 -j ${BB_NUMBER_THREADS} --with_branch_heads --with_tags
}
# do_sync[progress] = "outof:^\[(\d+)/(\d+)\]\s+"
addtask sync after do_fetch before do_patch

do_configure() {
    # TODO: do we need this?
    # Enable C++11 support at command line because the provided clang was compiled without C++11 support
    
    export CXX="${CXX} -std=c++11" 
    
    cd src
    export CHROMIUM_BUILDTOOLS_PATH=`pwd`/buildtools
    gn gen out/Release --args="import(\"//electron/build/args/release.gn\") $GN_EXTRA_ARGS target_cpu=\"${TARGET_ARCH}\" clang_base_path = \"${STAGING_DIR_NATIVE}/usr/local/bin\""
}

do_compile() {
    ninja -C out/Release electron 
}

#do_install() {
#    install -d      ${D}${libexecdir}/${BPN}
#    install -m 0755 ${S}/out/R/${BPN} ${D}${libexecdir}/${BPN}/
#    install -m 0644 ${S}/out/R/icudtl.dat ${D}${libexecdir}/${BPN}/
#    install -m 0644 ${S}/out/R/content_shell.pak ${D}${libexecdir}/${BPN}/
#    install -m 0644 ${S}/out/R/libffmpegsumo.so ${D}${libexecdir}/${BPN}/
#    install -m 0644 ${S}/out/R/libnode.so ${D}${libexecdir}/${BPN}/
#    install -m 0644 ${S}/out/R/snapshot_blob.bin ${D}${libexecdir}/${BPN}/
#    install -m 0644 ${S}/out/R/natives_blob.bin ${D}${libexecdir}/${BPN}/
#    install -d      ${D}${libexecdir}/${BPN}/locales
#    install -m 0644 ${S}/out/R/locales/* ${D}${libexecdir}/${BPN}/locales/
#    install -d      ${D}${bindir}/
#    ln -sf          ${libexecdir}/${BPN}/${BPN} ${D}${bindir}/${BPN}
#}
#
#do_clean() {
#    ./script/clean.py
#}

FILES_${PN} = "${bindir}/${PN} ${libexecdir}/${PN}/*"

TOOLCHAIN = "clang"
