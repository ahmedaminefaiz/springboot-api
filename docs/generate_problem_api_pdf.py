#!/usr/bin/env python3
"""Generate ProblemController API PDF."""

from pathlib import Path

from fpdf import FPDF

DOCS = Path(__file__).resolve().parent
MD_FILE = DOCS / "ProblemController-API.md"
PDF_FILE = DOCS / "ProblemController-API.pdf"


def to_latin1(text: str) -> str:
    text = (
        text.replace("\u2014", "-")
        .replace("\u2192", "->")
        .replace("\u2019", "'")
        .replace("\u00ab", '"')
        .replace("\u00bb", '"')
    )
    return text.encode("latin-1", "replace").decode("latin-1")


class ApiPdf(FPDF):
    def footer(self):
        self.set_y(-12)
        self.set_font("Helvetica", "I", 8)
        self.cell(0, 8, f"Page {self.page_no()}/{{nb}}", align="C")


def write_line(pdf: FPDF, text: str, h: float = 4.5):
    text = to_latin1(text.strip())
    if not text:
        pdf.ln(2)
        return
    w = pdf.w - pdf.l_margin - pdf.r_margin
    pdf.set_x(pdf.l_margin)
    pdf.multi_cell(w, h, text)


def main():
    lines = MD_FILE.read_text(encoding="utf-8").splitlines()
    pdf = ApiPdf()
    pdf.alias_nb_pages()
    pdf.set_auto_page_break(auto=True, margin=14)
    pdf.add_page()
    in_code = False

    for raw in lines:
        line = raw.rstrip()
        if line.startswith("```"):
            in_code = not in_code
            continue
        if in_code:
            pdf.set_font("Courier", "", 7)
            write_line(pdf, line, h=3.5)
            continue

        if line.startswith("# "):
            pdf.ln(3)
            pdf.set_font("Helvetica", "B", 13)
            write_line(pdf, line[2:], h=7)
        elif line.startswith("## "):
            pdf.ln(2)
            pdf.set_font("Helvetica", "B", 11)
            write_line(pdf, line[3:], h=6)
        elif line.startswith("### "):
            pdf.ln(1)
            pdf.set_font("Helvetica", "B", 10)
            write_line(pdf, line[4:], h=5)
        elif line.startswith("|") and "---" in line:
            continue
        elif line.startswith("|"):
            cells = [c.strip() for c in line.split("|") if c.strip()]
            pdf.set_font("Helvetica", "", 8)
            write_line(pdf, " | ".join(cells), h=4)
        elif line.startswith("- "):
            pdf.set_font("Helvetica", "", 9)
            write_line(pdf, "  - " + line[2:])
        else:
            pdf.set_font("Helvetica", "", 9)
            write_line(pdf, line)

    pdf.output(str(PDF_FILE))
    print(f"PDF written: {PDF_FILE}")


if __name__ == "__main__":
    main()
