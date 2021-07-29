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
"


PV = "7.2.4"


SRC_URI = " \
  git://github.com/electron/electron.git;tag=v${PV};nobranch=1 \
"

S = "${WORKDIR}/git"

inherit electron-arch

do_configure() {
    # Enable C++11 support at command line because the provided clang was compiled without C++11 support
    export CXX="${CXX} -std=c++11" 
    
    ./script/bootstrap.py -v --target_arch=${ELECTRON_ARCH}
}

do_compile() {
    ./script/build.py -c R
}

do_install() {
    install -d      ${D}${libexecdir}/${BPN}
    install -m 0755 ${S}/out/R/${BPN} ${D}${libexecdir}/${BPN}/
    install -m 0644 ${S}/out/R/icudtl.dat ${D}${libexecdir}/${BPN}/
    install -m 0644 ${S}/out/R/content_shell.pak ${D}${libexecdir}/${BPN}/
    install -m 0644 ${S}/out/R/libffmpegsumo.so ${D}${libexecdir}/${BPN}/
    install -m 0644 ${S}/out/R/libnode.so ${D}${libexecdir}/${BPN}/
    install -m 0644 ${S}/out/R/snapshot_blob.bin ${D}${libexecdir}/${BPN}/
    install -m 0644 ${S}/out/R/natives_blob.bin ${D}${libexecdir}/${BPN}/
    install -d      ${D}${libexecdir}/${BPN}/locales
    install -m 0644 ${S}/out/R/locales/* ${D}${libexecdir}/${BPN}/locales/
    install -d      ${D}${bindir}/
    ln -sf          ${libexecdir}/${BPN}/${BPN} ${D}${bindir}/${BPN}
}

do_clean() {
    ./script/clean.py
}

FILES_${PN} = "${bindir}/${PN} ${libexecdir}/${PN}/*"

TOOLCHAIN = "clang"
