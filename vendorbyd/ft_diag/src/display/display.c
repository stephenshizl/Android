#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdio.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <sys/types.h>
#include <time.h>
#include <linux/fb.h>
#include <linux/kd.h>
#include <pixelflinger/pixelflinger.h>
#include <cutils/memory.h>
#include "include/ft_sys.h"
#define FB_NUM 3
typedef struct
{
    GGLSurface texture;
    unsigned cwidth;
    unsigned cheight;
    unsigned ascent;
} GRFont;
#define FBLOCKFB                0x4689
#define FBUNLOCKFB              0x4690
#define FBDIAGEXIT            0x4691
#define FBDIAGSTOPFLUSH          0x4692
#define FBDIAGSTARTFLUSH          0x4693
static uint16_t black = 0x0000;
static uint16_t white = 0xffff;
static uint16_t red = 0xf800;
static uint16_t green = 0x07e0;
static uint16_t blue = 0x001f;
#define MSMFB_IOCTL_MAGIC 'm'
#define MSMFB_SUSPEND_SW_REFRESHER _IOW(MSMFB_IOCTL_MAGIC, 128, unsigned int)
#define MSMFB_RESUME_SW_REFRESHER _IOW(MSMFB_IOCTL_MAGIC, 129, unsigned int)
static GGLContext *gr_context = 0;
static GGLSurface gr_framebuffer[FB_NUM];
static unsigned gr_active_fb = 0;
static int gr_fb_fd =  - 1;
static int gr_vt_fd =  - 1;
static struct fb_var_screeninfo vi;
struct fb_fix_screeninfo fi;
struct timespec tv, tv2;
static void dumpinfo(struct fb_fix_screeninfo *fi, struct fb_var_screeninfo *vi);
#define PIXEL_FORMAT GGL_PIXEL_FORMAT_RGB_565
#define PIXEL_SIZE   2
static int get_framebuffer(GGLSurface *fb)
{
    int fd;
    void *bits;
    int i = 0;
    FT_LOG("%s\n", __func__);
    fd = open("/dev/graphics/fb0", O_RDWR);
    if (fd < 0)
    {
        FT_LOG("cannot open /dev/graphics/fb0, retrying with /dev/fb0\n");
        if ((fd = open("/dev/fb0", O_RDWR)) < 0)
        {
            fprintf(stderr, "cannot open /dev/fb0");
            return  - 1;
        }
    }
    if (ioctl(fd, FBIOGET_VSCREENINFO, &vi) < 0)
    {
        perror("failed to get fb0 info");
        close(fd);
        return  - 1;
    }
    vi.bits_per_pixel = PIXEL_SIZE * 8;
    if (PIXEL_FORMAT == GGL_PIXEL_FORMAT_BGRA_8888)
    {
        vi.red.offset = 8;
        vi.red.length = 8;
        vi.green.offset = 16;
        vi.green.length = 8;
        vi.blue.offset = 24;
        vi.blue.length = 8;
        vi.transp.offset = 0;
        vi.transp.length = 8;
    }
    else if (PIXEL_FORMAT == GGL_PIXEL_FORMAT_RGBX_8888)
    {
        vi.red.offset = 24;
        vi.red.length = 8;
        vi.green.offset = 16;
        vi.green.length = 8;
        vi.blue.offset = 8;
        vi.blue.length = 8;
        vi.transp.offset = 0;
        vi.transp.length = 8;
    }
    else
    {
         /* RGB565*/
        vi.red.offset = 11;
        vi.red.length = 5;
        vi.green.offset = 5;
        vi.green.length = 6;
        vi.blue.offset = 0;
        vi.blue.length = 5;
        vi.transp.offset = 0;
        vi.transp.length = 0;
    }
    if (ioctl(fd, FBIOPUT_VSCREENINFO, &vi) < 0)
    {
        perror("failed to put fb0 info");
        close(fd);
        return  - 1;
    }
    if (ioctl(fd, FBIOGET_FSCREENINFO, &fi) < 0)
    {
        perror("failed to get fb0 info");
        close(fd);
        return  - 1;
    }
    dumpinfo(&fi, &vi);
    fb->stride = fi.line_length / PIXEL_SIZE;
    bits = mmap(0, fi.smem_len, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (bits == MAP_FAILED)
    {
        perror("failed to mmap framebuffer");
        close(fd);
        return  - 1;
    }
    #if 0
        memset(bits, 0x0, fi.smem_len);
        fb->version = sizeof(*fb);
        fb->width = vi.xres;
        fb->height = vi.yres;
        fb->stride = fi.line_length / PIXEL_SIZE;
        fb->data = (void*)(((unsigned)bits));
        fb->format = PIXEL_FORMAT;
        memset(fb->data, 0, vi.yres *fb->stride *PIXEL_SIZE);
        fb++;
        fb->version = sizeof(*fb);
        fb->width = vi.xres;
        fb->height = vi.yres;
        fb->stride = fi.line_length / PIXEL_SIZE;
        fb->data = (void*)(((unsigned)bits) + (vi.yres *fb->stride *vi.bits_per_pixel / 8));
        fb->format = PIXEL_FORMAT;
        memset(fb->data, 0, vi.yres *fb->stride *PIXEL_SIZE);
        fb++;

    for (i = 0; i < FB_NUM; i++)
    {
        FT_LOG("fb %d\n", i);
        fb->version = sizeof(*fb);
        fb->width = vi.xres;
        fb->height = vi.yres;
        fb->stride = fi.line_length / PIXEL_SIZE;
        fb->data = (void*)(((unsigned)bits) + i *(vi.yres *fb->stride
                    *vi.bits_per_pixel / 8));
        fb->format = PIXEL_FORMAT;
        memset(fb->data, 0, vi.yres *fb->stride *PIXEL_SIZE);
        fb++;
    }

        vi.bits_per_pixel = PIXEL_SIZE * 8;
        /* RGB565*/
        vi.red.offset = 11;
        vi.red.length = 5;
        vi.green.offset = 5;
        vi.green.length = 6;
        vi.blue.offset = 0;
        vi.blue.length = 5;
        vi.transp.offset = 0;
        vi.transp.length = 0;
        if (ioctl(fd, FBIOPUT_VSCREENINFO, &vi) < 0)
        {
            perror("failed to put fb0 info");
            close(fd);
            return  - 1;
        }
    #endif
    return fd;
}
static void set_active_framebuffer(unsigned n)
{
    if (n >= FB_NUM)
    {
        return ;
    }
    FT_LOG("%s\n", __func__);
    vi.yres_virtual = vi.yres * FB_NUM;
    vi.yoffset = (n) *vi.yres;
    #if 1
        vi.bits_per_pixel = PIXEL_SIZE * 8;
        /* RGB565*/
        vi.red.offset = 11;
        vi.red.length = 5;
        vi.green.offset = 5;
        vi.green.length = 6;
        vi.blue.offset = 0;
        vi.blue.length = 5;
        vi.transp.offset = 0;
        vi.transp.length = 0;
    #endif
    if (ioctl(gr_fb_fd, FBIOPUT_VSCREENINFO, &vi) < 0)
    {
        fprintf(stderr, "active fb swap failed!\n");
    }
    else
    {
        FT_LOG("[%s,%d]active buffer: %d\n", __func__, __LINE__, n);
    }
}
static void dumpinfo(struct fb_fix_screeninfo *fi, struct fb_var_screeninfo *vi)
{
    fprintf(stderr, "vi.xres = %d\n", vi->xres);
    fprintf(stderr, "vi.yres = %d\n", vi->yres);
    fprintf(stderr, "vi.xresv = %d\n", vi->xres_virtual);
    fprintf(stderr, "vi.yresv = %d\n", vi->yres_virtual);
    fprintf(stderr, "vi.xoff = %d\n", vi->xoffset);
    fprintf(stderr, "vi.yoff = %d\n", vi->yoffset);
    fprintf(stderr, "vi.bits_per_pixel = %d\n", vi->bits_per_pixel);
    fprintf(stderr, "vi.grayscale = %d\n", vi->grayscale);
    fprintf(stderr, "fi.smem_len = %d\n", fi->smem_len);
    fprintf(stderr, "fi.line_length = %d\n", fi->line_length);
}
int gr_init(void)
{
    int fd =  - 1;
    int w;
    int h;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    if ( - 1 != gr_fb_fd)
    {
        FT_LOG("[%s,%d ]gr_init aready \n", __func__, __LINE__);
        return 0;
    }
    ioctl(gr_fb_fd, FBDIAGSTOPFLUSH, &vi);
    if (!access("/dev/tty0", F_OK))
    {
        fd = open("/dev/tty0", O_RDWR | O_SYNC);
        if (fd < 0)
        {
            return  - 1;
        }
        if (ioctl(fd, KDSETMODE, (void*)KD_GRAPHICS))
        {
            close(fd);
            return  - 1;
        }
    }
    gr_fb_fd = get_framebuffer(gr_framebuffer);
    if (gr_fb_fd < 0)
    {
        if (fd >= 0)
        {
            ioctl(fd, KDSETMODE, (void*)KD_TEXT);
            close(fd);
        }
        return  - 1;
    }
    gr_vt_fd = fd;
    /* start with 0 as front (displayed) and 1 as back (drawing) */
    gr_active_fb = 0;
    set_active_framebuffer(0);
    ioctl(gr_fb_fd, FBLOCKFB, &vi);
    w = vi.xres;
    h = vi.yres;
    clear_screen(w, h, (uint16_t*)gr_framebuffer[0].data);
    clear_screen(w, h, (uint16_t*)gr_framebuffer[1].data);
    clear_screen(w, h, (uint16_t*)gr_framebuffer[2].data);
    FT_LOG("[%s,%d] End...\n", __func__, __LINE__);
    return 0;
}
void gr_exit(void)
{
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    ioctl(gr_fb_fd, FBDIAGEXIT, &vi);
    ioctl(gr_fb_fd, FBDIAGSTARTFLUSH, &vi);
    close(gr_fb_fd);
    gr_fb_fd =  - 1;
    if (gr_vt_fd >= 0)
    {
        ioctl(gr_vt_fd, KDSETMODE, (void*)KD_TEXT);
        close(gr_vt_fd);
        gr_vt_fd =  - 1;
    }
}
int gr_fb_width(void)
{
    return gr_framebuffer[0].width;
}
int gr_fb_height(void)
{
    return gr_framebuffer[0].height;
}
uint32_t rgb565torgba8888(unsigned short rgb565)
{
    uint32_t rgb32, red, green, blue, alpha;
    /* convert 16 bits to 32 bits */
    rgb32 = ((rgb565 >> 11) &0x1F);
    red = (rgb32 << 3) | (rgb32 >> 2);
    rgb32 = ((rgb565 >> 5) &0x3F);
    green = (rgb32 << 2) | (rgb32 >> 4);
    rgb32 = ((rgb565) &0x1F);
    blue = (rgb32 << 3) | (rgb32 >> 2);
    alpha = 0xff;
    rgb32 = (alpha << 24) | (blue << 16) | (green << 8) | (red);
    return rgb32;
}
void draw_grid(int w, int h, uint16_t *loc)
{
    int i, j;
    int v;
    int stride = gr_framebuffer->stride;
        //fi.line_length / (vi.bits_per_pixel >> 3);
    for (j = 0; j < h / 2; j++)
    {
        for (i = 0; i < w / 2; i++)
        {
            loc[i + j *(stride)] = red;
        }
        for (; i < w; i++)
        {
            loc[i + j *(stride)] = green;
        }
    }
    for (; j < h; j++)
    {
        for (i = 0; i < w / 2; i++)
        {
            loc[i + j *(stride)] = blue;
        }
        for (; i < w; i++)
        {
            loc[i + j *(stride)] = white;
        }
    }
}
void draw_zebra_v(int w, int h, uint16_t *loc)
{
    int i, j;
    int stride = gr_framebuffer->stride;
        //fi.line_length / (vi.bits_per_pixel >> 3);
    for (i = 0; i < w; i++)
    {
        for (j = 0; j < h / 5; j++)
        {
            loc[i + j *(stride)] = red;
        }
        for (j = h / 5; j < h / 5 * 2; j++)
        {
            loc[i + j *(stride)] = green;
        }
        for (j = h / 5 * 2; j < h / 5 * 3; j++)
        {
            loc[i + j *(stride)] = blue;
        }
        for (j = h / 5 * 3; j < h / 5 * 4; j++)
        {
            loc[i + j *(stride)] = black;
        }
        for (j = h / 5 * 4; j < h; j++)
        {
            loc[i + j *(stride)] = white;
        }
    }
}
void draw_zebra_h(int w, int h, uint16_t *loc)
{
    int i, j;
    int stride = gr_framebuffer->stride;
        //fi.line_length / (vi.bits_per_pixel >> 3);
    uint16_t color = 0;
    for (i = 0; i < w; i++)
    {
        if (i == 0)
        {
            color = red;
        }
        else if (i == w / 5)
        {
            color = green;
        }
        else if (i == w / 5 * 2)
        {
            color = blue;
        }
        else if (i == w / 5 * 3)
        {
            color = black;
        }
        else if (i == w / 5 * 4)
        {
            color = white;
        }
        for (j = 0; j < h; j++)
        {
            loc[j *(stride) + i] = color;
        }
    }
}
void draw_white_margin(int w, int h, uint16_t *loc)
{
    int i, j;
    int stride = gr_framebuffer->stride;
        //fi.line_length / (vi.bits_per_pixel >> 3);
    uint16_t color = 0;
    int b_width = 1;
    for (i = 0; i < b_width; i++)
    {
        color = white;
        for (j = 0; j < h; j++)
        {
            loc[j *(stride) + i] = color;
        }
    }
    for (i = w - b_width; i < w; i++)
    {
        color = white;
        for (j = 0; j < h; j++)
        {
            loc[j *(stride) + i] = color;
        }
    }
    for (i = b_width; i < w - b_width; i++)
    {
        color = white;
        for (j = 0; j < b_width; j++)
        {
            loc[j *(stride) + i] = color;
        }
    }
    for (i = b_width; i < w - b_width; i++)
    {
        color = white;
        for (j = h - b_width; j < h; j++)
        {
            loc[j *(stride) + i] = color;
        }
    }
    for (i = b_width; i < w - b_width; i++)
    {
        color = black;
        for (j = b_width; j < h - b_width; j++)
        {
            loc[j *(stride) + i] = color;
        }
    }
}
void draw_black_margin(int w, int h, uint16_t *loc)
{
    int i, j;
    int stride = gr_framebuffer->stride;
        //fi.line_length / (vi.bits_per_pixel >> 3);
    uint16_t color = 0;
    int b_width = 1;
    for (i = 0; i < b_width; i++)
    {
        color = black;
        for (j = 0; j < h; j++)
        {
            loc[j *(stride) + i] = color;
        }
    }
    for (i = w - b_width; i < w; i++)
    {
        color = black;
        for (j = 0; j < h; j++)
        {
            loc[j *(stride) + i] = color;
        }
    }
    for (i = b_width; i < w - b_width; i++)
    {
        color = black;
        for (j = 0; j < b_width; j++)
        {
            loc[j *(stride) + i] = color;
        }
    }
    for (i = b_width; i < w - b_width; i++)
    {
        color = black;
        for (j = h - b_width; j < h; j++)
        {
            loc[j *(stride) + i] = color;
        }
    }
    for (i = b_width; i < w - b_width; i++)
    {
        color = white;
        for (j = b_width; j < h - b_width; j++)
        {
            loc[j *(stride) + i] = color;
        }
    }
}
//zxd added for e802t start
void draw_flicker(int w, int h, uint16_t *loc)
{
    int i, j;
    int temp;
    int stride = gr_framebuffer->stride;
        //fi.line_length / (vi.bits_per_pixel >> 3);
    for (i = 0; i < w; i++)
    {
        for (j = 0; j < h; j++)
        {
            temp = j % 6;
            if ((temp == 0) || (temp == 1) || (temp == 2))
            {
                loc[j *(stride) + i] = white;
            }
            else
            {
                loc[j *(stride) + i] = black;
            }
        }
    }
}
void draw_grayscale(int w, int h, uint16_t *loc)
{
    int i, j;
    int stride = gr_framebuffer->stride;
        //fi.line_length / (vi.bits_per_pixel >> 3);
    uint16_t color = 0;
    for (i = 0; i < w; i++)
    {
        for (j = 0; j < 43; j++)
        {
             /* black = 0x0000,white = 0xffff
            step = 0xffff/854 = 76;
             */
            loc[j *(stride) + i] = black;
            //         fprintf(stderr, "line = %d,collor = %x \n", j,0xff000000);
        }
        for (j = 43; j < 854-48;)
        {
            color = 0xff000000 + 0x010101 *(j - 40) / 3;
            loc[j *(stride) + i] = color;
            loc[(j + 1)*(stride) + i] = color;
            loc[(j + 2)*(stride) + i] = color;
            j = j + 3;
            //         fprintf(stderr, "line = %d,collor = %x \n", j,color );
        }
        for (j = 854-48; j < 854; j++)
        {
            loc[j *(stride) + i] = white;
            //         fprintf(stderr, "line = %d,collor = %x \n", j,0xffffffff);
        }
    }
}
void draw_rgb_row(int w, int h, uint16_t *loc)
{
    int i, j;
    int stride = gr_framebuffer->stride;
        //fi.line_length / (vi.bits_per_pixel >> 3);
    for (i = 0; i < w; i++)
    {
        for (j = 0; j < h; j++)
        {
            //854/3=284
            if (j >= 0 && j < h / 3)
            {
                loc[j *(stride) + i] = red;
            }
            else if (j >= h / 3 && j < 2 *h / 3)
            {
                loc[j *(stride) + i] = green;
            }
            else
            {
                loc[j *(stride) + i] = blue;
            }
        }
    }
}
void draw_white_black(int w, int h, uint16_t *loc)
{
    int i, j;
    int stride = gr_framebuffer->stride;
        //fi.line_length / (vi.bits_per_pixel >> 3);
    for (i = 0; i < w; i++)
    {
        for (j = 0; j < h; j++)
         \
            {
            if (i >= 0 && i < w / 2)
            {
                loc[j *(stride) + i] = white;
            }
            else
            {
                loc[j *(stride) + i] = black;
            }
        }
    }
}
//zxd added for e802t end.
void draw_color(int w, int h, uint16_t color, uint16_t *loc)
{
    int i, j;
    int stride = gr_framebuffer->stride;
        //fi.line_length / (vi.bits_per_pixel >> 3);
    FT_LOG("[%s,%d]:stride %d\n", __func__, __LINE__, stride);
    for (j = 0; j < h; j++)
    {
        for (i = 0; i < w; i++)
        {
            loc[i + j *(stride)] = color;
        }
    }
}
void clear_screen(int w, int h, uint16_t *loc)
{
    int i, j;
    int stride = gr_framebuffer->stride;
        //fi.line_length / (vi.bits_per_pixel >> 3);
    for (j = 0; j < h; j++)
        for (i = 0; i < w; i++)
        {
            loc[i + j *(stride)] = black;
        }
}
//int ft_display_test(int type,int width,int height)
int ft_display_test(int type)
{
    int w;
    int h;
    uint16_t color = 0;
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    gr_init();
    w = vi.xres;
    h = vi.yres;
    #if 0
        if (width > 0)
        {
            w = width;
        }
        if (height > 0)
        {
            h = height;
        }
    #endif
    ioctl(gr_fb_fd, FBUNLOCKFB, &vi);
    FT_LOG("[%s,%d] enter type %x\n", __func__, __LINE__, type);
    switch (type)
    {
        case S_GRID:
            draw_grid(w, h, (uint16_t*)gr_framebuffer[0].data);
            draw_grid(w, h, (uint16_t*)gr_framebuffer[1].data);
            draw_grid(w, h, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_ZEBRA_H:
            draw_zebra_h(w, h, (uint16_t*)gr_framebuffer[0].data);
            draw_zebra_h(w, h, (uint16_t*)gr_framebuffer[1].data);
            draw_zebra_h(w, h, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_ZEBRA_V:
            draw_zebra_v(w, h, (uint16_t*)gr_framebuffer[0].data);
            draw_zebra_v(w, h, (uint16_t*)gr_framebuffer[1].data);
            draw_zebra_v(w, h, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_RED:
            color = red;
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[0].data);
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[1].data);
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_GREEN:
            color = green;
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[0].data);
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[1].data);
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_BLUE:
            color = blue;
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[0].data);
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[1].data);
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_BLACK:
            color = black;
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[0].data);
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[1].data);
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_WHITE:
            color = white;
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[0].data);
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[1].data);
            draw_color(w, h, color, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_WHITE_M:
            draw_white_margin(w, h, (uint16_t*)gr_framebuffer[0].data);
            draw_white_margin(w, h, (uint16_t*)gr_framebuffer[1].data);
            draw_white_margin(w, h, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_BLACK_M:
            draw_black_margin(w, h, (uint16_t*)gr_framebuffer[0].data);
            draw_black_margin(w, h, (uint16_t*)gr_framebuffer[1].data);
            draw_black_margin(w, h, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_FLICKER:
            draw_flicker(w, h, (uint16_t*)gr_framebuffer[0].data);
            draw_flicker(w, h, (uint16_t*)gr_framebuffer[1].data);
            draw_flicker(w, h, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_GRAYSCALE:
            draw_grayscale(w, h, (uint16_t*)gr_framebuffer[0].data);
            draw_grayscale(w, h, (uint16_t*)gr_framebuffer[1].data);
            draw_grayscale(w, h, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_RGB_ROW:
            draw_rgb_row(w, h, (uint16_t*)gr_framebuffer[0].data);
            draw_rgb_row(w, h, (uint16_t*)gr_framebuffer[1].data);
            draw_rgb_row(w, h, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_WHITE_BLACK:
            draw_white_black(w, h, (uint16_t*)gr_framebuffer[0].data);
            draw_white_black(w, h, (uint16_t*)gr_framebuffer[1].data);
            draw_white_black(w, h, (uint16_t*)gr_framebuffer[2].data);
            break;
        case S_INIT:
            gr_init();
            break;
        case S_EXIT:
            gr_exit();
            return type;
            break;
        default:
            //gr_exit();
            ioctl(gr_fb_fd, FBUNLOCKFB, &vi);
            gr_exit();
            return type;
    }
    fprintf(stderr, "%lld\n", (tv2.tv_sec *1000000000LL + tv2.tv_nsec) - (tv.tv_sec *1000000000LL + tv.tv_nsec));
    set_active_framebuffer(0);
    ioctl(gr_fb_fd, FBIOPAN_DISPLAY, &vi);
    ioctl(gr_fb_fd, FBLOCKFB, &vi);
    FT_LOG("[%s,%d]End type%x...\n", __func__, __LINE__, type);
    return type;
}

int ft_display_test_lte(int type)
{
    FT_LOG("[%s,%d] Start...\n", __func__, __LINE__);
    FT_LOG("[%s,%d] enter type %x\n", __func__, __LINE__, type);
    switch (type)
    {
        case S_GRID:
            system("input tap 885 316");//queding
            break;
        case S_ZEBRA_H:
            system("am start -n com.qti.factory/com.qti.factory.Bluetooth.Bluetooth");
            break;
        case S_ZEBRA_V:
            system("am start -n com.qti.factory/com.qti.factory.WiFi.WiFi");
            break;
        case S_RED:
            system("am start -n com.qti.factory/com.qti.factory.diag.DiagappLcd");
            break;
        case S_GREEN:
            system("input tap 337 590"); //add for adb pop press ok
            break;
        case S_BLUE:
            // system("input tap 334 443");
            system("input keyevent 4"); ///backkey
            break;
        case S_BLACK:
            system("am start -n com.qti.factory/com.qti.factory.GPS.GPS_BYD");
            break;
        case S_WHITE:
            system("input swipe 118 1157 558 1135 "); //choose video
            break;
        case S_WHITE_M:
            system("input tap 106 927");//video button
            break;
        case S_BLACK_M:
            system("setenforce 0;am start -n com.goodix.rawdata/.RawDataTest");
            break;
        case S_FLICKER:
           // system("input tap 334 443");
           system("am start -n com.android.camera2/com.android.camera.CameraLauncher");
            break;
        case S_GRAYSCALE:
           // system("input tap 858 970");
            system("input tap 527 1719");//record, stop
            break;
        case S_RGB_ROW:
           system("input tap 720 1749"); //delete
            break;
        case S_WHITE_BLACK:
            //system("input keyevent 6");
            system("input swipe 893 1283 391 1350");//display video
            break;
        case S_INIT:
            system("input keyevent 26");//powerkey
            break;
        case S_UNLOCK:
            system("input swipe 400 600 100 600");
            break;
        case S_HAVEDEL:
             system("input tap 419 1633");
            break;
        case S_PASS:
             system("input tap 178 1751"); //pass key position
            break;
        case S_FAIL:
             system("input tap 780 1788");//fail key position
            break;
        case S_EXIT:
           // system("input tap 0 0");
           system("input tap 551 941");//play
            return type;
            break;
        default:
            //gr_exit();
            ioctl(gr_fb_fd, FBUNLOCKFB, &vi);
            gr_exit();
            return type;
    }
    fprintf(stderr, "%lld\n", (tv2.tv_sec *1000000000LL + tv2.tv_nsec) - (tv.tv_sec *1000000000LL + tv.tv_nsec));
    set_active_framebuffer(0);
    ioctl(gr_fb_fd, FBIOPAN_DISPLAY, &vi);
    ioctl(gr_fb_fd, FBLOCKFB, &vi);
    FT_LOG("[%s,%d]End type%x...\n", __func__, __LINE__, type);
    return type;
}
