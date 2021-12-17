// My Rust is a bit rusty, by which I mean I haven't used it in forever, by which I mean I've never used it...

use std::fs;
use std::io::{self, stdout, BufWriter};
use std::cmp;

use ferris_says::say; // Let's have an adorable crab say the answer, because Rust

fn main() {
    // Parse input into a 2-D Vector of i32
    let input_string = fs::read_to_string("input.txt").expect("file din work");
    let input_lines: Vec<_> = input_string.split_terminator("\n").collect();
    let mut dumbo_matrix: Vec<Vec<i32>> = vec![];
    for line in input_lines {
        let line_as_chars: Vec<_> = line.trim().split("").filter(|&s| !s.is_empty()).collect();
        let line_as_nums: Vec<i32> = line_as_chars.iter().map(|s| s.parse::<i32>().unwrap()).collect();
        dumbo_matrix.push(line_as_nums);
    }
    let mut total_flash_count = 0;
    let mut first_flash = -1;
    let mut all_flashed = false;
    let mut step = 0;

    // Part 1: "simulate 100 steps"; part 2: "What is the first step during which all octopuses flash?"
    while !all_flashed || step < 100 {
        // "First, the energy level of each octopus increases by 1."
        // "Then, any octopus with an energy level greater than 9 flashes."
        for x in 0..dumbo_matrix.len() {
            for y in 0..dumbo_matrix[x].len() {
                let flash_count = increment_and_flash(x, y, &mut dumbo_matrix);
                if step < 100 {
                    total_flash_count += flash_count;
                }
            }
        }

        all_flashed = true;
        for x in 0..dumbo_matrix.len() {
            for y in 0..dumbo_matrix[x].len() {
                if dumbo_matrix[x][y] == -1 {
                    // "Finally, any octopus that flashed during this step has its energy level set to 0"
                    dumbo_matrix[x][y] = 0;
                } else {
                    all_flashed = false;
                }
            }
        }

        if all_flashed {
            first_flash = step + 1;
        }
        step += 1;
    }

    have_ferris_say(&format!("The first time all eleph—er, octopi flashed was step {} \
        and the total number of flashes after 100 steps was {}!", first_flash, total_flash_count)).unwrap();
}

/// Helper method for getting a crustacean to say a thing
fn have_ferris_say(message: &str) -> Result<(), io::Error> {
    let stdout = stdout();
    let width = cmp::min(message.chars().count(), 40);
    let mut writer = BufWriter::new(stdout.lock());
    say(message.as_bytes(), width, &mut writer)
}

/// Increment the energy level at the given coordinates and flash if necessary
fn increment_and_flash(x: usize, y: usize, matrix: &mut Vec<Vec<i32>>) -> i32 {
    if matrix[x][y] == -1 { return 0 }

    matrix[x][y] += 1;
    if matrix[x][y] > 9 {
        return flash(x, y, matrix)
    }
    0
}

/// Make the octopus at the given coordinates flash
fn flash(x: usize, y: usize, matrix: &mut Vec<Vec<i32>>) -> i32 {
    matrix[x][y] = -1; // Placeholder to indicate this one flashed

    let mut flash_count = 1;

    let min_x = cmp::max(0, x as i32 - 1) as usize;
    let max_x = cmp::min(matrix.len() - 1, x + 1);
    let min_y = cmp::max(0, y as i32 - 1) as usize;
    let max_y = cmp::min(matrix[0].len() - 1, y + 1);

    for neighbor_x in min_x..=max_x {
        for neighbor_y in min_y..=max_y {
            flash_count += increment_and_flash(neighbor_x, neighbor_y, matrix);
        }
    }
    flash_count
}
